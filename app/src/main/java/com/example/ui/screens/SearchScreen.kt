package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Qari
import com.example.data.model.QariCatalog
import com.example.data.model.RevelationType
import com.example.data.model.Surah
import com.example.data.model.SurahCatalog
import com.example.ui.theme.CelestialBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class SearchFilter(val label: String) {
    ALL("All"),
    SURAHS("Surahs"),
    RECITERS("Reciters"),
    MECCAN("Meccan"),
    MEDINAN("Medinan")
}

@Composable
fun SearchScreen(
    currentQari: Qari,
    onSelectQari: (Qari) -> Unit,
    onPlaySurah: (Qari, Surah) -> Unit,
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    val allReciters = remember { QariCatalog.getAllQaris(context) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SearchFilter.ALL) }

    val cleanQuery = searchQuery.trim().lowercase()

    val matchedQaris = remember(cleanQuery, selectedFilter, allReciters) {
        if (selectedFilter == SearchFilter.SURAHS || selectedFilter == SearchFilter.MECCAN || selectedFilter == SearchFilter.MEDINAN) {
            emptyList()
        } else if (cleanQuery.isBlank()) {
            QariCatalog.featuredQaris
        } else {
            allReciters.filter {
                it.name.lowercase().contains(cleanQuery) || it.arabicName.contains(cleanQuery)
            }
        }
    }

    val matchedSurahs = remember(cleanQuery, selectedFilter) {
        if (selectedFilter == SearchFilter.RECITERS) {
            emptyList()
        } else {
            SurahCatalog.allSurahs.filter { surah ->
                val matchesFilter = when (selectedFilter) {
                    SearchFilter.MECCAN -> surah.revelationType == RevelationType.MECCAN
                    SearchFilter.MEDINAN -> surah.revelationType == RevelationType.MEDINAN
                    else -> true
                }
                val matchesQuery = cleanQuery.isBlank() ||
                        surah.name.lowercase().contains(cleanQuery) ||
                        surah.arabicName.contains(cleanQuery) ||
                        surah.englishMeaning.lowercase().contains(cleanQuery) ||
                        surah.number.toString() == cleanQuery

                matchesFilter && matchesQuery
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Search Input
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Search Quran & Reciters",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, meaning, or number...", color = TextSecondary) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
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
                    modifier = Modifier.fillMaxWidth().testTag("input_search_main")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SearchFilter.values()) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CelestialBlue,
                                selectedLabelColor = SpaceBlack,
                                containerColor = Color.White.copy(alpha = 0.08f),
                                labelColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Matched Reciters Section
        if (matchedQaris.isNotEmpty()) {
            item {
                Text(
                    text = "Reciters (${matchedQaris.size})",
                    color = GoldAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
            items(matchedQaris, key = { "qari_${it.id}" }) { qari ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                        .clickable { onSelectQari(qari) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QariAvatar(
                            qari = qari,
                            modifier = Modifier.size(44.dp),
                            borderWidth = 1.5.dp,
                            borderColor = GoldAccent
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = qari.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "${qari.arabicName} • ${qari.location}", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Matched Surahs Section
        if (matchedSurahs.isNotEmpty()) {
            item {
                Text(
                    text = "Surahs (${matchedSurahs.size})",
                    color = CelestialBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
            items(matchedSurahs, key = { "surah_${it.number}" }) { surah ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                        .clickable { onPlaySurah(currentQari, surah) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${surah.number}",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${surah.name} (${surah.englishMeaning})",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
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
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = SpaceBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
