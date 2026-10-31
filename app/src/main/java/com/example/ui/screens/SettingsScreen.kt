package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CelestialBlue
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceMidnight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    onNavigateToReminders: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onOpenAmbientSettings: () -> Unit,
    contentPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Settings Bar Header with Developer Name
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Settings & Preferences",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "QuranPlayer • Recitation & Ambience",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Developer Tag Badge in Setting Bar
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldAccent.copy(alpha = 0.15f))
                            .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Developer",
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DEV",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Dedicated Prominent Developer Card: ASLAM KHAN DEVELOPER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                SpaceMidnight,
                                Color(0xFF162D63)
                            )
                        )
                    )
                    .border(1.5.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                    .padding(18.dp)
                    .testTag("developer_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Developer Avatar Icon with Glowing Ring
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(GoldAccent)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Developer",
                            tint = SpaceBlack,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ASLAM KHAN DEVELOPER",
                                color = GoldAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = CelestialCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Lead Android Developer & Creator",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = CelestialBlue,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "aslamkhanmahar64@gmail.com",
                                color = CelestialBlue,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        // QuranPlayer Logo Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, GoldAccent, RoundedCornerShape(14.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_quran_player_logo),
                            contentDescription = "QuranPlayer Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(text = "QuranPlayer", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Created by ASLAM KHAN DEVELOPER", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Version 6.0.0 • Premium Free Edition", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Settings items
        item {
            SettingRowItem(
                icon = Icons.Default.Alarm,
                iconTint = CelestialBlue,
                title = "Recitation Reminders",
                subtitle = "Manage daily recitation times & alarms",
                onClick = onNavigateToReminders,
                tag = "setting_reminders"
            )
        }

        item {
            SettingRowItem(
                icon = Icons.Default.GraphicEq,
                iconTint = GoldAccent,
                title = "Background Ambience & Sound Mix",
                subtitle = "Quranify video themes: Fire, Ocean, Birds, Rain",
                onClick = onOpenAmbientSettings,
                tag = "setting_ambient"
            )
        }

        item {
            SettingRowItem(
                icon = Icons.Default.Star,
                iconTint = GoldAccent,
                title = "Bookmarked Recitations",
                subtitle = "Saved surahs and favorite Qurra",
                onClick = onNavigateToBookmarks,
                tag = "setting_bookmarks"
            )
        }

        item {
            SettingRowItem(
                icon = Icons.Default.MenuBook,
                iconTint = CelestialBlue,
                title = "Recitation Streak & Goal",
                subtitle = "Daily recitation target (15 minutes/day)",
                onClick = {},
                tag = "setting_goal"
            )
        }

        item {
            SettingRowItem(
                icon = Icons.Default.Person,
                iconTint = EmeraldGreen,
                title = "Developer Credit",
                subtitle = "ASLAM KHAN DEVELOPER • aslamkhanmahar64@gmail.com",
                onClick = {},
                tag = "setting_developer_credit"
            )
        }

        item {
            SettingRowItem(
                icon = Icons.Default.Favorite,
                iconTint = RoseAccent,
                title = "Made for the Ummah",
                subtitle = "Free, ad-free, authentic recitation for everyone worldwide",
                onClick = {},
                tag = "setting_about"
            )
        }
    }
}

@Composable
private fun SettingRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(tag)
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
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = subtitle, color = TextSecondary, fontSize = 12.sp)
                }
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Open", tint = TextSecondary)
        }
    }
}
