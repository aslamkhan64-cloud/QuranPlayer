package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import com.example.data.model.AmbientCatalog
import com.example.data.model.AmbientSound
import com.example.data.model.Qari
import com.example.data.model.QariCatalog
import com.example.data.model.Surah
import com.example.data.model.SurahCatalog
import com.example.data.storage.QuranPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentQari: Qari = QariCatalog.defaultQari,
    val currentSurah: Surah = SurahCatalog.allSurahs[0],
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val quranVolume: Float = 1.0f,
    val ambientVolume: Float = 0.5f,
    val selectedAmbient: AmbientSound = AmbientCatalog.NONE,
    val sleepTimerMinutesLeft: Int = 0,
    val isCaptionVisible: Boolean = false,
    val errorMessage: String? = null
) {
    val progressFraction: Float
        get() = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}

class QuranAudioManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val prefs = QuranPreferences(context)

    private var recitationPlayer: MediaPlayer? = null
    private var ambientPlayer: MediaPlayer? = null

    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null

    private val _uiState = MutableStateFlow(
        PlayerUiState(
            quranVolume = prefs.getQuranVolume(),
            ambientVolume = prefs.getAmbientVolume(),
            selectedAmbient = AmbientCatalog.getById(prefs.getSelectedAmbientId())
        )
    )
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        // Restore recent session if available
        val recent = prefs.getRecentSession()
        if (recent != null) {
            val qari = QariCatalog.getById(recent.qariId)
            val surah = SurahCatalog.getByNumber(recent.surahNumber) ?: SurahCatalog.allSurahs[0]
            _uiState.value = _uiState.value.copy(
                currentQari = qari,
                currentSurah = surah,
                currentPositionMs = recent.positionMs
            )
        }
    }

    fun playSurah(qari: Qari, surah: Surah, startPositionMs: Int = 0) {
        val current = _uiState.value
        _uiState.value = current.copy(
            currentQari = qari,
            currentSurah = surah,
            isLoading = true,
            errorMessage = null,
            currentPositionMs = startPositionMs
        )

        val audioUrl = qari.getSurahAudioUrl(surah.number)
        setupRecitationPlayer(audioUrl, qari, surah, startPositionMs)

        // Save recent session
        prefs.saveRecentSession(qari.id, surah.number, startPositionMs)
    }

    private fun setupRecitationPlayer(
        url: String,
        qari: Qari,
        surah: Surah,
        startPositionMs: Int,
        isFallbackAttempt: Boolean = false
    ) {
        try {
            recitationPlayer?.release()
            recitationPlayer = MediaPlayer().apply {
                setDataSource(url)
                val vol = _uiState.value.quranVolume
                setVolume(vol, vol)

                setOnPreparedListener { mp ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPlaying = true,
                        durationMs = mp.duration
                    )
                    if (startPositionMs > 0 && startPositionMs < mp.duration) {
                        mp.seekTo(startPositionMs)
                    }
                    applySpeed(_uiState.value.playbackSpeed)
                    mp.start()

                    // Start concurrent ambient audio if enabled
                    startAmbientAudio()
                    startProgressTracker()
                }

                setOnCompletionListener {
                    // Auto-play next surah
                    playNext()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e("QuranPlayer", "MediaPlayer error: what=$what extra=$extra on url=$url")
                    if (!isFallbackAttempt && qari.fallbackServerUrl.isNotEmpty()) {
                        val fallbackUrl = surah.getAudioUrl(qari.fallbackServerUrl)
                        setupRecitationPlayer(fallbackUrl, qari, surah, startPositionMs, isFallbackAttempt = true)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isPlaying = false,
                            errorMessage = "Streaming error. Retrying or choose another reciter."
                        )
                    }
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isPlaying = false,
                errorMessage = "Failed to load audio: ${e.message}"
            )
        }
    }

    fun togglePlayPause() {
        val mp = recitationPlayer
        if (mp == null) {
            // Start playing current surah
            playSurah(_uiState.value.currentQari, _uiState.value.currentSurah)
            return
        }

        if (mp.isPlaying) {
            mp.pause()
            ambientPlayer?.pause()
            _uiState.value = _uiState.value.copy(isPlaying = false)
            stopProgressTracker()
        } else {
            mp.start()
            ambientPlayer?.start()
            _uiState.value = _uiState.value.copy(isPlaying = true)
            startProgressTracker()
        }
    }

    fun seekTo(positionMs: Int) {
        val mp = recitationPlayer ?: return
        val clamped = positionMs.coerceIn(0, _uiState.value.durationMs)
        mp.seekTo(clamped)
        _uiState.value = _uiState.value.copy(currentPositionMs = clamped)
    }

    fun playNext() {
        val currentSurahNum = _uiState.value.currentSurah.number
        val nextNum = if (currentSurahNum < 114) currentSurahNum + 1 else 1
        val nextSurah = SurahCatalog.getByNumber(nextNum) ?: return
        playSurah(_uiState.value.currentQari, nextSurah)
    }

    fun playPrevious() {
        val currentSurahNum = _uiState.value.currentSurah.number
        val prevNum = if (currentSurahNum > 1) currentSurahNum - 1 else 114
        val prevSurah = SurahCatalog.getByNumber(prevNum) ?: return
        playSurah(_uiState.value.currentQari, prevSurah)
    }

    fun setSpeed(speed: Float) {
        _uiState.value = _uiState.value.copy(playbackSpeed = speed)
        applySpeed(speed)
    }

    private fun applySpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                recitationPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        mp.playbackParams = PlaybackParams().apply { this.speed = speed }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Ambient Sound System with concurrent dual-playback
    fun setAmbientSound(ambient: AmbientSound) {
        _uiState.value = _uiState.value.copy(selectedAmbient = ambient)
        prefs.setSelectedAmbientId(ambient.id)
        startAmbientAudio()
    }

    private fun startAmbientAudio() {
        ambientPlayer?.stop()
        ambientPlayer?.release()
        ambientPlayer = null

        val ambient = _uiState.value.selectedAmbient
        val rawRes = ambient.rawResId ?: return

        try {
            ambientPlayer = MediaPlayer.create(context, rawRes)?.apply {
                isLooping = true
                val vol = _uiState.value.ambientVolume
                setVolume(vol, vol)
                if (_uiState.value.isPlaying) {
                    start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setQuranVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _uiState.value = _uiState.value.copy(quranVolume = clamped)
        prefs.setQuranVolume(clamped)
        try {
            recitationPlayer?.setVolume(clamped, clamped)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAmbientVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _uiState.value = _uiState.value.copy(ambientVolume = clamped)
        prefs.setAmbientVolume(clamped)
        try {
            ambientPlayer?.setVolume(clamped, clamped)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Sleep Timer
    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _uiState.value = _uiState.value.copy(sleepTimerMinutesLeft = 0)
            return
        }

        _uiState.value = _uiState.value.copy(sleepTimerMinutesLeft = minutes)
        sleepTimerJob = scope.launch {
            var mins = minutes
            while (mins > 0 && isActive) {
                delay(60_000L)
                mins--
                _uiState.value = _uiState.value.copy(sleepTimerMinutesLeft = mins)
            }
            // Timer expired -> pause everything gently
            if (isActive) {
                recitationPlayer?.pause()
                ambientPlayer?.pause()
                _uiState.value = _uiState.value.copy(isPlaying = false, sleepTimerMinutesLeft = 0)
                stopProgressTracker()
            }
        }
    }

    fun toggleCaption() {
        _uiState.value = _uiState.value.copy(isCaptionVisible = !_uiState.value.isCaptionVisible)
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                recitationPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val currentPos = mp.currentPosition
                        _uiState.value = _uiState.value.copy(
                            currentPositionMs = currentPos,
                            durationMs = mp.duration
                        )
                    }
                }
                delay(300L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        sleepTimerJob?.cancel()
        recitationPlayer?.release()
        recitationPlayer = null
        ambientPlayer?.release()
        ambientPlayer = null
    }
}
