package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecitationReminder
import com.example.data.model.SurahCatalog
import com.example.reminder.QuranReminderScheduler
import com.example.ui.theme.CelestialBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceMidnight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    reminders: List<RecitationReminder>,
    onToggleReminder: (String) -> Unit,
    onAddReminder: (RecitationReminder) -> Unit,
    onDeleteReminder: (String) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    // Android 13+ Notification Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                QuranReminderScheduler.createNotificationChannel(context)
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("button_back_reminders")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Daily Recitation Reminders",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Never miss your scheduled Quran recitation session",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Notification permission banner on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CelestialBlue.copy(alpha = 0.15f))
                            .border(1.dp, CelestialBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
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
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Notification",
                                    tint = CelestialBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Enable Notifications",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Ensure daily alerts appear on time",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CelestialBlue,
                                    contentColor = SpaceBlack
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "Grant", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Empty state
            if (reminders.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "No Reminders",
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Recitation Reminders Set",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the + button below to create your daily recitation schedule.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Reminders List
            items(reminders, key = { it.id }) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    onToggle = { onToggleReminder(reminder.id) },
                    onDelete = { onDeleteReminder(reminder.id) },
                    onTestNotification = {
                        QuranReminderScheduler.triggerTestNotification(context, reminder)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Floating Action Button to Add Reminder
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = GoldAccent,
            contentColor = SpaceBlack,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 110.dp, end = 20.dp)
                .testTag("fab_add_reminder")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Recitation Reminder"
            )
        }
    }

    // Add Reminder Dialog
    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newReminder ->
                onAddReminder(newReminder)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ReminderCard(
    reminder: RecitationReminder,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTestNotification: () -> Unit
) {
    val targetSurah = reminder.targetSurahNumber?.let { SurahCatalog.getByNumber(it) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (reminder.isEnabled) Color.White.copy(alpha = 0.08f)
                else Color.White.copy(alpha = 0.03f)
            )
            .border(
                1.dp,
                if (reminder.isEnabled) CelestialBlue.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(20.dp)
            )
            .padding(18.dp)
            .testTag("reminder_card_${reminder.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time & Status
                Column {
                    Text(
                        text = reminder.formattedTime,
                        color = if (reminder.isEnabled) GoldAccent else TextSecondary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reminder.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Toggle Switch
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SpaceBlack,
                        checkedTrackColor = GoldAccent,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("switch_reminder_${reminder.id}")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Surah Tag & Days info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (targetSurah != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CelestialBlue.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "📖 ${targetSurah.name} (${targetSurah.arabicName})",
                                color = CelestialBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = reminder.repeatSummary,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Actions: Test notification & Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onTestNotification,
                        modifier = Modifier.testTag("test_notify_${reminder.id}")
                    ) {
                        Text(
                            text = "Test",
                            color = CelestialBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp).testTag("delete_reminder_${reminder.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Reminder",
                            tint = RoseAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (RecitationReminder) -> Unit
) {
    var title by remember { mutableStateOf("Daily Recitation") }
    var selectedHour by remember { mutableIntStateOf(6) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var isAm by remember { mutableStateOf(true) }
    var selectedSurahNumber by remember { mutableStateOf<Int?>(36) } // Default Surah Ya-Sin
    var surahDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SpaceMidnight,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "New Recitation Reminder",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Preset Quick Templates
                Text(
                    text = "Quick Presets:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                title = "Morning (Fajr)"
                                selectedHour = 6
                                selectedMinute = 0
                                isAm = true
                                selectedSurahNumber = 36 // Ya-Sin
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌅 Fajr (6 AM)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                title = "Nightly (Al-Mulk)"
                                selectedHour = 9
                                selectedMinute = 30
                                isAm = false
                                selectedSurahNumber = 67 // Al-Mulk
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌙 Night (9:30)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CelestialBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = CelestialBlue,
                        unfocusedLabelColor = TextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_reminder_title")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Time Selection (Hour, Minute, AM/PM)
                Text(
                    text = "Recitation Time",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .clickable {
                                    selectedHour = if (selectedHour == 12) 1 else selectedHour + 1
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = String.format("%02d", selectedHour),
                                color = GoldAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(" : ", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        // Minute selector
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .clickable {
                                    selectedMinute = (selectedMinute + 15) % 60
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = String.format("%02d", selectedMinute),
                                color = GoldAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // AM / PM Switch Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CelestialBlue)
                            .clickable { isAm = !isAm }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = if (isAm) "AM" else "PM",
                            color = SpaceBlack,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Surah to recite selector
                Text(text = "Target Surah", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = surahDropdownExpanded,
                    onExpandedChange = { surahDropdownExpanded = it }
                ) {
                    val currentSurahName = selectedSurahNumber?.let { num ->
                        SurahCatalog.getByNumber(num)?.let { "${it.number}. ${it.name} (${it.arabicName})" }
                    } ?: "General Quran Session"

                    OutlinedTextField(
                        value = currentSurahName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = surahDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CelestialBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = surahDropdownExpanded,
                        onDismissRequest = { surahDropdownExpanded = false },
                        modifier = Modifier.background(SpaceMidnight)
                    ) {
                        DropdownMenuItem(
                            text = { Text("General Quran Session", color = TextPrimary) },
                            onClick = {
                                selectedSurahNumber = null
                                surahDropdownExpanded = false
                            }
                        )
                        // Common recitation surahs first
                        listOf(36, 67, 18, 55, 56, 1, 2).mapNotNull { SurahCatalog.getByNumber(it) }.forEach { s ->
                            DropdownMenuItem(
                                text = { Text("${s.number}. ${s.name} (${s.arabicName})", color = GoldAccent) },
                                onClick = {
                                    selectedSurahNumber = s.number
                                    surahDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hour24 = if (isAm) {
                        if (selectedHour == 12) 0 else selectedHour
                    } else {
                        if (selectedHour == 12) 12 else selectedHour + 12
                    }
                    val reminder = RecitationReminder(
                        id = UUID.randomUUID().toString(),
                        title = title.ifBlank { "Daily Recitation" },
                        hour = hour24,
                        minute = selectedMinute,
                        isEnabled = true,
                        targetSurahNumber = selectedSurahNumber
                    )
                    onSave(reminder)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = SpaceBlack
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("button_save_reminder")
            ) {
                Text("Set Reminder", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
