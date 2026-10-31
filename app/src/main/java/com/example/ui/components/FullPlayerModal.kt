package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlayerUiState
import com.example.data.model.Surah
import com.example.data.model.SurahCatalog
import com.example.ui.theme.CelestialBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceMidnight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerModal(
    uiState: PlayerUiState,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onSleepTimerChange: (Int) -> Unit,
    onOpenAmbientDialog: () -> Unit,
    onSurahSelectedFromQueue: (Surah) -> Unit,
    onDismiss: () -> Unit
) {
    var showQueueSheet by remember { mutableStateOf(false) }
    var showSpeedSheet by remember { mutableStateOf(false) }
    var showSleepSheet by remember { mutableStateOf(false) }
    var isArabicTextExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
    ) {
        // Dynamic Motion Video Background (Quranify fire, ocean, birds, rain, or cosmos)
        AmbientVideoBackground(
            ambientType = uiState.selectedAmbient.type,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle dark translucent scrim for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("full_player_close")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse Player",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Ambient Pill Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .clickable { onOpenAmbientDialog() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("button_open_ambient")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Ambience",
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiState.selectedAmbient.name,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = onOpenAmbientDialog,
                    modifier = Modifier.testTag("button_open_mix")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Audio Volume Mix",
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Center Artwork & Calligraphy Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Real Reciter Photo with glowing halo
                QariAvatar(
                    qari = uiState.currentQari,
                    modifier = Modifier
                        .size(180.dp)
                        .shadow(elevation = 20.dp, shape = CircleShape, spotColor = GoldAccent),
                    borderWidth = 3.dp,
                    borderColor = GoldAccent.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Surah Name (English + Arabic)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.currentSurah.name,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(36.dp).testTag("button_toggle_bookmark")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) GoldAccent else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Arabic Surah Calligraphy & Reciter Details
                Text(
                    text = uiState.currentSurah.arabicName,
                    color = GoldAccent,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${uiState.currentQari.name} • ${uiState.currentSurah.revelationType.label} (${uiState.currentSurah.versesCount} Ayahs)",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                // Optional Arabic recitation caption toggle
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedVisibility(
                    visible = isArabicTextExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.currentSurah.previewAyah,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }
            }

            // Scrubber / Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = uiState.progressFraction,
                    onValueChange = { fraction ->
                        val targetMs = (fraction * uiState.durationMs).toInt()
                        onSeek(targetMs)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = GoldAccent,
                        activeTrackColor = GoldAccent,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("slider_progress")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(uiState.currentPositionMs),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatDuration(uiState.durationMs),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Main Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { showSpeedSheet = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("button_speed")
                ) {
                    Text(
                        text = "${uiState.playbackSpeed}x",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Previous
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.size(48.dp).testTag("button_prev")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Surah",
                        tint = TextPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play / Pause
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = GoldAccent,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        IconButton(
                            onClick = onPlayPause,
                            modifier = Modifier.size(72.dp).testTag("button_play_pause")
                        ) {
                            Icon(
                                imageVector = if (uiState.isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                                tint = GoldAccent,
                                modifier = Modifier.size(68.dp)
                            )
                        }
                    }
                }

                // Next
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(48.dp).testTag("button_next")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Surah",
                        tint = TextPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Sleep Timer Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (uiState.sleepTimerMinutesLeft > 0) CelestialBlue.copy(alpha = 0.3f)
                            else Color.White.copy(alpha = 0.12f)
                        )
                        .clickable { showSleepSheet = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("button_sleep_timer")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Sleep Timer",
                            tint = if (uiState.sleepTimerMinutesLeft > 0) CelestialBlue else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        if (uiState.sleepTimerMinutesLeft > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.sleepTimerMinutesLeft}m",
                                color = CelestialBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bottom Tools Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Arabic Verse Display Toggle
                IconButton(
                    onClick = { isArabicTextExpanded = !isArabicTextExpanded },
                    modifier = Modifier.testTag("button_toggle_arabic_text")
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Arabic Calligraphy Verse",
                        tint = if (isArabicTextExpanded) GoldAccent else TextSecondary
                    )
                }

                // Ambience Button
                IconButton(
                    onClick = onOpenAmbientDialog,
                    modifier = Modifier.testTag("button_open_ambient_bottom")
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Ambience Sound",
                        tint = CelestialBlue
                    )
                }

                // Playlist / Queue Button
                IconButton(
                    onClick = { showQueueSheet = true },
                    modifier = Modifier.testTag("button_open_queue")
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Surah Queue",
                        tint = TextSecondary
                    )
                }
            }
        }
    }

    // Modal Sheet: Speed Selector
    if (showSpeedSheet) {
        val speedState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showSpeedSheet = false },
            sheetState = speedState,
            containerColor = SpaceMidnight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Playback Speed",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (uiState.playbackSpeed == speed) CelestialBlue.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable {
                                onSpeedChange(speed)
                                showSpeedSheet = false
                            }
                            .padding(vertical = 14.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (speed == 1.0f) "1.0x (Normal)" else "${speed}x",
                            color = if (uiState.playbackSpeed == speed) GoldAccent else TextPrimary,
                            fontWeight = if (uiState.playbackSpeed == speed) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet: Sleep Timer Selector
    if (showSleepSheet) {
        val sleepState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showSleepSheet = false },
            sheetState = sleepState,
            containerColor = SpaceMidnight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Recitation Sleep Timer",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Automatically stops recitation when you fall asleep",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                listOf(
                    0 to "Turn Off Timer",
                    15 to "15 Minutes",
                    30 to "30 Minutes",
                    45 to "45 Minutes",
                    60 to "1 Hour"
                ).forEach { (minutes, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (uiState.sleepTimerMinutesLeft == minutes) CelestialBlue.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable {
                                onSleepTimerChange(minutes)
                                showSleepSheet = false
                            }
                            .padding(vertical = 14.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            color = if (uiState.sleepTimerMinutesLeft == minutes) GoldAccent else TextPrimary,
                            fontWeight = if (uiState.sleepTimerMinutesLeft == minutes) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet: Surah Queue
    if (showQueueSheet) {
        val queueState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showQueueSheet = false },
            sheetState = queueState,
            containerColor = SpaceMidnight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Continue Reciting (${uiState.currentQari.name})",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showQueueSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    items(SurahCatalog.allSurahs) { surah ->
                        val isCurrent = surah.number == uiState.currentSurah.number
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrent) CelestialBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.04f))
                                .clickable {
                                    onSurahSelectedFromQueue(surah)
                                    showQueueSheet = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isCurrent) GoldAccent else Color.White.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${surah.number}",
                                        color = if (isCurrent) SpaceBlack else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = surah.name,
                                        color = if (isCurrent) GoldAccent else TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${surah.versesCount} Ayahs • ${surah.revelationType.label}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Text(
                                text = surah.arabicName,
                                color = GoldAccent,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Serif
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

private fun formatDuration(millis: Int): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
