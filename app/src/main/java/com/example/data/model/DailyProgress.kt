package com.example.data.model

data class Bookmark(
    val id: String,
    val surahNumber: Int,
    val surahName: String,
    val arabicName: String,
    val qariId: String,
    val qariName: String,
    val addedAt: Long = System.currentTimeMillis(),
    val note: String = ""
)

data class RecitationReminder(
    val id: String,
    val title: String,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean = true,
    val targetSurahNumber: Int? = null,
    val repeatDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7) // 1=Sun .. 7=Sat
) {
    val formattedTime: String
        get() {
            val h = if (hour == 0 || hour == 12) 12 else hour % 12
            val m = String.format("%02d", minute)
            val amPm = if (hour < 12) "AM" else "PM"
            return "$h:$m $amPm"
        }

    val repeatSummary: String
        get() = if (repeatDays.size == 7) {
            "Every day"
        } else if (repeatDays == setOf(2, 3, 4, 5, 6)) {
            "Weekdays"
        } else if (repeatDays == setOf(1, 7)) {
            "Weekends"
        } else {
            val names = listOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            repeatDays.sorted().joinToString(", ") { names.getOrElse(it) { "" } }
        }
}

data class DailyProgress(
    val todayMinutes: Int = 0,
    val dailyGoalMinutes: Int = 15,
    val currentStreak: Int = 3,
    val lastListenedDate: String = "",
    val totalSurahsCompleted: Int = 12
)
