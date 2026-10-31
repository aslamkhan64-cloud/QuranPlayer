package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.QuranAudioManager
import com.example.data.model.Qari
import com.example.data.model.QariCatalog
import com.example.data.model.RecitationReminder
import com.example.data.model.Surah
import com.example.data.model.SurahCatalog
import com.example.data.storage.QuranPreferences
import com.example.reminder.QuranReminderScheduler
import com.example.ui.components.AmbientSoundDialog
import com.example.ui.components.FullPlayerModal
import com.example.ui.components.GalaxyMotionBackground
import com.example.ui.components.MiniPlayer
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QariProfileScreen
import com.example.ui.screens.RecitersScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CelestialBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.QuranPlayerTheme
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceMidnight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AppScreen {
    HOME,
    RECITERS,
    QARI_PROFILE,
    BOOKMARKS,
    REMINDERS,
    SEARCH,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    private lateinit var audioManager: QuranAudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioManager = QuranAudioManager(this)
        QuranReminderScheduler.createNotificationChannel(this)

        // Check if launched with specific surah number intent
        val playSurahNum = intent.getIntExtra("play_surah_num", -1)
        if (playSurahNum > 0) {
            val surah = SurahCatalog.getByNumber(playSurahNum)
            if (surah != null) {
                audioManager.playSurah(QariCatalog.defaultQari, surah)
            }
        }

        setContent {
            QuranPlayerTheme {
                QuranPlayerApp(
                    audioManager = audioManager,
                    onFinishActivity = { finish() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.release()
    }
}

@Composable
fun QuranPlayerApp(
    audioManager: QuranAudioManager,
    onFinishActivity: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { QuranPreferences(context) }

    val playerUiState by audioManager.uiState.collectAsState()

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedQari by remember { mutableStateOf(QariCatalog.defaultQari) }
    var isFullPlayerVisible by remember { mutableStateOf(false) }
    var isAmbientDialogOpen by remember { mutableStateOf(false) }

    var bookmarks by remember { mutableStateOf(prefs.getBookmarks()) }
    var reminders by remember { mutableStateOf(prefs.getReminders()) }
    var favoriteQariIds by remember { mutableStateOf(prefs.getFavoriteQariIds()) }
    var dailyProgress by remember { mutableStateOf(prefs.getDailyProgress()) }

    // Sync reminders on change
    fun refreshReminders() {
        reminders = prefs.getReminders()
        QuranReminderScheduler.scheduleAll(context)
    }

    fun refreshBookmarks() {
        bookmarks = prefs.getBookmarks()
    }

    val isCurrentSurahBookmarked = remember(bookmarks, playerUiState.currentQari.id, playerUiState.currentSurah.number) {
        bookmarks.any { it.qariId == playerUiState.currentQari.id && it.surahNumber == playerUiState.currentSurah.number }
    }

    // Handle back button for sub-screens
    BackHandler(enabled = isFullPlayerVisible || currentScreen != AppScreen.HOME) {
        if (isFullPlayerVisible) {
            isFullPlayerVisible = false
        } else if (currentScreen == AppScreen.QARI_PROFILE ||
            currentScreen == AppScreen.BOOKMARKS ||
            currentScreen == AppScreen.REMINDERS
        ) {
            currentScreen = AppScreen.HOME
        } else {
            currentScreen = AppScreen.HOME
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
    ) {
        // Celestial starry cosmos background
        GalaxyMotionBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    // Floating MiniPlayer docked above navigation bar
                    AnimatedVisibility(
                        visible = !isFullPlayerVisible,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        MiniPlayer(
                            uiState = playerUiState,
                            onPlayPause = { audioManager.togglePlayPause() },
                            onNext = { audioManager.playNext() },
                            onClick = { isFullPlayerVisible = true }
                        )
                    }

                    // Bottom Navigation Bar
                    NavigationBar(
                        containerColor = SpaceMidnight.copy(alpha = 0.95f),
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.HOME,
                            onClick = { currentScreen = AppScreen.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 11.sp) },
                            colors = navItemColors(),
                            modifier = Modifier.testTag("nav_home")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.RECITERS || currentScreen == AppScreen.QARI_PROFILE,
                            onClick = { currentScreen = AppScreen.RECITERS },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Reciters") },
                            label = { Text("Reciters", fontSize = 11.sp) },
                            colors = navItemColors(),
                            modifier = Modifier.testTag("nav_reciters")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.REMINDERS,
                            onClick = { currentScreen = AppScreen.REMINDERS },
                            icon = { Icon(Icons.Default.Alarm, contentDescription = "Reminders") },
                            label = { Text("Reminders", fontSize = 11.sp) },
                            colors = navItemColors(),
                            modifier = Modifier.testTag("nav_reminders")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.SEARCH,
                            onClick = { currentScreen = AppScreen.SEARCH },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text("Search", fontSize = 11.sp) },
                            colors = navItemColors(),
                            modifier = Modifier.testTag("nav_search")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.SETTINGS,
                            onClick = { currentScreen = AppScreen.SETTINGS },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label = { Text("Settings", fontSize = 11.sp) },
                            colors = navItemColors(),
                            modifier = Modifier.testTag("nav_settings")
                        )
                    }
                }
            }
        ) { paddingValues ->
            when (currentScreen) {
                AppScreen.HOME -> {
                    val nextActiveReminder = reminders.firstOrNull { it.isEnabled }
                    HomeScreen(
                        playerState = playerUiState,
                        dailyProgress = dailyProgress,
                        nextReminder = nextActiveReminder,
                        onPlaySurah = { qari, surah ->
                            audioManager.playSurah(qari, surah)
                            isFullPlayerVisible = true
                        },
                        onOpenQari = { qari ->
                            selectedQari = qari
                            currentScreen = AppScreen.QARI_PROFILE
                        },
                        onNavigateToBookmarks = { currentScreen = AppScreen.BOOKMARKS },
                        onNavigateToReminders = { currentScreen = AppScreen.REMINDERS },
                        onNavigateToReciters = { currentScreen = AppScreen.RECITERS },
                        contentPadding = paddingValues
                    )
                }

                AppScreen.RECITERS -> {
                    RecitersScreen(
                        favoriteQariIds = favoriteQariIds,
                        onToggleFavoriteQari = { qariId ->
                            prefs.toggleFavoriteQari(qariId)
                            favoriteQariIds = prefs.getFavoriteQariIds()
                        },
                        onSelectQari = { qari ->
                            selectedQari = qari
                            currentScreen = AppScreen.QARI_PROFILE
                        },
                        contentPadding = paddingValues
                    )
                }

                AppScreen.QARI_PROFILE -> {
                    QariProfileScreen(
                        qari = selectedQari,
                        currentSurahNumber = playerUiState.currentSurah.number,
                        isPlaying = playerUiState.isPlaying,
                        isBookmarked = { surahNum ->
                            bookmarks.any { it.qariId == selectedQari.id && it.surahNumber == surahNum }
                        },
                        onToggleBookmark = { surahNum ->
                            prefs.toggleBookmark(selectedQari.id, surahNum)
                            refreshBookmarks()
                        },
                        onPlaySurah = { surah ->
                            audioManager.playSurah(selectedQari, surah)
                            isFullPlayerVisible = true
                        },
                        onBack = { currentScreen = AppScreen.RECITERS },
                        contentPadding = paddingValues
                    )
                }

                AppScreen.BOOKMARKS -> {
                    BookmarksScreen(
                        bookmarks = bookmarks,
                        onPlayBookmark = { qari, surah ->
                            audioManager.playSurah(qari, surah)
                            isFullPlayerVisible = true
                        },
                        onRemoveBookmark = { qariId, surahNum ->
                            prefs.toggleBookmark(qariId, surahNum)
                            refreshBookmarks()
                        },
                        onBack = { currentScreen = AppScreen.HOME },
                        contentPadding = paddingValues
                    )
                }

                AppScreen.REMINDERS -> {
                    RemindersScreen(
                        reminders = reminders,
                        onToggleReminder = { id ->
                            prefs.toggleReminder(id)
                            refreshReminders()
                        },
                        onAddReminder = { newReminder ->
                            prefs.addOrUpdateReminder(newReminder)
                            refreshReminders()
                        },
                        onDeleteReminder = { id ->
                            prefs.deleteReminder(id)
                            refreshReminders()
                        },
                        onBack = { currentScreen = AppScreen.HOME },
                        contentPadding = paddingValues
                    )
                }

                AppScreen.SEARCH -> {
                    SearchScreen(
                        currentQari = playerUiState.currentQari,
                        onSelectQari = { qari ->
                            selectedQari = qari
                            currentScreen = AppScreen.QARI_PROFILE
                        },
                        onPlaySurah = { qari, surah ->
                            audioManager.playSurah(qari, surah)
                            isFullPlayerVisible = true
                        },
                        contentPadding = paddingValues
                    )
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        onNavigateToReminders = { currentScreen = AppScreen.REMINDERS },
                        onNavigateToBookmarks = { currentScreen = AppScreen.BOOKMARKS },
                        onOpenAmbientSettings = { isAmbientDialogOpen = true },
                        contentPadding = paddingValues
                    )
                }
            }
        }

        // Full Screen Player Modal with swipe down & dynamic ambient video themes
        AnimatedVisibility(
            visible = isFullPlayerVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            FullPlayerModal(
                uiState = playerUiState,
                isBookmarked = isCurrentSurahBookmarked,
                onToggleBookmark = {
                    prefs.toggleBookmark(playerUiState.currentQari.id, playerUiState.currentSurah.number)
                    refreshBookmarks()
                },
                onPlayPause = { audioManager.togglePlayPause() },
                onSeek = { audioManager.seekTo(it) },
                onNext = { audioManager.playNext() },
                onPrevious = { audioManager.playPrevious() },
                onSpeedChange = { audioManager.setSpeed(it) },
                onSleepTimerChange = { audioManager.setSleepTimer(it) },
                onOpenAmbientDialog = { isAmbientDialogOpen = true },
                onSurahSelectedFromQueue = { surah ->
                    audioManager.playSurah(playerUiState.currentQari, surah)
                },
                onDismiss = { isFullPlayerVisible = false }
            )
        }

        // Background Ambience Sound & Mix Dialog
        if (isAmbientDialogOpen) {
            AmbientSoundDialog(
                selectedSound = playerUiState.selectedAmbient,
                quranVolume = playerUiState.quranVolume,
                ambientVolume = playerUiState.ambientVolume,
                onSoundSelected = { sound ->
                    audioManager.setAmbientSound(sound)
                },
                onQuranVolumeChanged = { audioManager.setQuranVolume(it) },
                onAmbientVolumeChanged = { audioManager.setAmbientVolume(it) },
                onDismiss = { isAmbientDialogOpen = false }
            )
        }
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = SpaceBlack,
    selectedTextColor = GoldAccent,
    indicatorColor = GoldAccent,
    unselectedIconColor = TextSecondary,
    unselectedTextColor = TextSecondary
)
