package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.ui.components.QariAvatar
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Qari
import com.example.data.model.Surah
import com.example.data.model.SurahCatalog
import com.example.ui.theme.CelestialBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QariProfileScreen(
    qari: Qari,
    currentSurahNumber: Int,
    isPlaying: Boolean,
    isBookmarked: (Int) -> Boolean,
    onToggleBookmark: (Int) -> Unit,
    onPlaySurah: (Surah) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SurahCatalog.allSurahs.take(qari.totalSurahs)
        } else {
            val q = searchQuery.trim().lowercase()
            SurahCatalog.allSurahs.take(qari.totalSurahs).filter {
                it.name.lowercase().contains(q) ||
                it.arabicName.contains(q) ||
                it.number.toString() == q ||
                it.englishMeaning.lowercase().contains(q)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Back Button & Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("button_back_profile")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reciter Profile",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Qari Portrait & Info Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QariAvatar(
                    qari = qari,
                    modifier = Modifier
                        .size(130.dp)
                        .shadow(elevation = 16.dp, shape = CircleShape, spotColor = GoldAccent),
                    borderWidth = 3.dp,
                    borderColor = GoldAccent
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = qari.name,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = qari.arabicName,
                    color = GoldAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${qari.location} • ${qari.totalSurahs} Surahs Recorded",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Search Input
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Surah name or number...", color = TextSecondary) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CelestialBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_search_surah_profile")
                )
            }
        }

        // List of Surahs
        items(filteredSurahs, key = { it.number }) { surah ->
            val isCurrent = surah.number == currentSurahNumber
            val bookmarked = isBookmarked(surah.number)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isCurrent) CelestialBlue.copy(alpha = 0.2f)
                        else Color.White.copy(alpha = 0.04f)
                    )
                    .border(
                        1.dp,
                        if (isCurrent) CelestialBlue else Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onPlaySurah(surah) }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .testTag("surah_item_${surah.number}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Surah Number badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isCurrent) GoldAccent else Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${surah.number}",
                                color = if (isCurrent) SpaceBlack else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = surah.name,
                                    color = if (isCurrent) GoldAccent else TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${surah.englishMeaning})",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "${surah.versesCount} Ayahs • ${surah.revelationType.label}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = surah.arabicName,
                            color = GoldAccent,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        // Bookmark star
                        IconButton(
                            onClick = { onToggleBookmark(surah.number) },
                            modifier = Modifier.size(36.dp).testTag("bookmark_surah_${surah.number}")
                        ) {
                            Icon(
                                imageVector = if (bookmarked) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Bookmark",
                                tint = if (bookmarked) GoldAccent else TextMuted
                            )
                        }

                        // Play icon
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isCurrent && isPlaying) GoldAccent else Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = if (isCurrent && isPlaying) SpaceBlack else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
