package com.example.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Bookmark
import com.example.data.model.DailyProgress
import com.example.data.model.QariCatalog
import com.example.data.model.RecitationReminder
import com.example.data.model.SurahCatalog
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuranPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("quran_player_prefs", Context.MODE_PRIVATE)

    // Bookmarks
    fun getBookmarks(): List<Bookmark> {
        val jsonString = prefs.getString("bookmarks_json", null) ?: return emptyList()
        val list = mutableListOf<Bookmark>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Bookmark(
                        id = obj.getString("id"),
                        surahNumber = obj.getInt("surahNumber"),
                        surahName = obj.getString("surahName"),
                        arabicName = obj.optString("arabicName", ""),
                        qariId = obj.getString("qariId"),
                        qariName = obj.getString("qariName"),
                        addedAt = obj.optLong("addedAt", System.currentTimeMillis()),
                        note = obj.optString("note", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveBookmarks(bookmarks: List<Bookmark>) {
        val jsonArray = JSONArray()
        bookmarks.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("surahNumber", it.surahNumber)
                put("surahName", it.surahName)
                put("arabicName", it.arabicName)
                put("qariId", it.qariId)
                put("qariName", it.qariName)
                put("addedAt", it.addedAt)
                put("note", it.note)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("bookmarks_json", jsonArray.toString()).apply()
    }

    fun toggleBookmark(qariId: String, surahNumber: Int): Boolean {
        val current = getBookmarks().toMutableList()
        val existingIndex = current.indexOfFirst { it.qariId == qariId && it.surahNumber == surahNumber }
        val isNowBookmarked: Boolean
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
            isNowBookmarked = false
        } else {
            val qari = QariCatalog.getById(qariId)
            val surah = SurahCatalog.getByNumber(surahNumber)
            if (surah != null) {
                current.add(
                    Bookmark(
                        id = "${qariId}_$surahNumber",
                        surahNumber = surahNumber,
                        surahName = surah.name,
                        arabicName = surah.arabicName,
                        qariId = qari.id,
                        qariName = qari.name,
                        addedAt = System.currentTimeMillis()
                    )
                )
            }
            isNowBookmarked = true
        }
        saveBookmarks(current)
        return isNowBookmarked
    }

    fun isBookmarked(qariId: String, surahNumber: Int): Boolean {
        return getBookmarks().any { it.qariId == qariId && it.surahNumber == surahNumber }
    }

    // Favorite Qaris
    fun getFavoriteQariIds(): Set<String> {
        return prefs.getStringSet("favorite_qaris", setOf("yasser", "mossad", "mishary")) ?: emptySet()
    }

    fun toggleFavoriteQari(qariId: String): Boolean {
        val current = getFavoriteQariIds().toMutableSet()
        val isFav = if (current.contains(qariId)) {
            current.remove(qariId)
            false
        } else {
            current.add(qariId)
            true
        }
        prefs.edit().putStringSet("favorite_qaris", current).apply()
        return isFav
    }

    // Recent Playback
    data class RecentSession(
        val qariId: String,
        val surahNumber: Int,
        val positionMs: Int = 0,
        val timestamp: Long = System.currentTimeMillis()
    )

    fun getRecentSession(): RecentSession? {
        val qariId = prefs.getString("recent_qari_id", null) ?: return null
        val surahNum = prefs.getInt("recent_surah_num", 1)
        val pos = prefs.getInt("recent_pos_ms", 0)
        val time = prefs.getLong("recent_timestamp", 0)
        return RecentSession(qariId, surahNum, pos, time)
    }

    fun saveRecentSession(qariId: String, surahNumber: Int, positionMs: Int) {
        prefs.edit()
            .putString("recent_qari_id", qariId)
            .putInt("recent_surah_num", surahNumber)
            .putInt("recent_pos_ms", positionMs)
            .putLong("recent_timestamp", System.currentTimeMillis())
            .apply()

        // Also track daily progress
        incrementDailyTime(1)
    }

    // Daily Recitation Reminder System
    fun getReminders(): List<RecitationReminder> {
        val jsonString = prefs.getString("reminders_json", null)
        if (jsonString == null) {
            // Default initial reminders for daily recitation!
            val defaults = listOf(
                RecitationReminder(
                    id = "reminder_fajr",
                    title = "Morning Recitation (Fajr)",
                    hour = 6,
                    minute = 0,
                    isEnabled = true,
                    targetSurahNumber = 36 // Surah Ya-Sin
                ),
                RecitationReminder(
                    id = "reminder_mulk",
                    title = "Nightly Recitation (Surah Al-Mulk)",
                    hour = 21,
                    minute = 30,
                    isEnabled = true,
                    targetSurahNumber = 67 // Surah Al-Mulk
                ),
                RecitationReminder(
                    id = "reminder_kahf",
                    title = "Friday Recitation (Surah Al-Kahf)",
                    hour = 10,
                    minute = 0,
                    isEnabled = false,
                    targetSurahNumber = 18,
                    repeatDays = setOf(6) // Friday (6)
                )
            )
            saveReminders(defaults)
            return defaults
        }

        val list = mutableListOf<RecitationReminder>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val daysArray = obj.optJSONArray("repeatDays")
                val daysSet = mutableSetOf<Int>()
                if (daysArray != null) {
                    for (d in 0 until daysArray.length()) {
                        daysSet.add(daysArray.getInt(d))
                    }
                } else {
                    daysSet.addAll(listOf(1, 2, 3, 4, 5, 6, 7))
                }
                list.add(
                    RecitationReminder(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        hour = obj.getInt("hour"),
                        minute = obj.getInt("minute"),
                        isEnabled = obj.optBoolean("isEnabled", true),
                        targetSurahNumber = if (obj.has("targetSurahNumber") && !obj.isNull("targetSurahNumber")) obj.getInt("targetSurahNumber") else null,
                        repeatDays = daysSet
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveReminders(reminders: List<RecitationReminder>) {
        val jsonArray = JSONArray()
        reminders.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("title", it.title)
                put("hour", it.hour)
                put("minute", it.minute)
                put("isEnabled", it.isEnabled)
                if (it.targetSurahNumber != null) {
                    put("targetSurahNumber", it.targetSurahNumber)
                }
                val daysArr = JSONArray()
                it.repeatDays.forEach { day -> daysArr.put(day) }
                put("repeatDays", daysArr)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("reminders_json", jsonArray.toString()).apply()
    }

    fun addOrUpdateReminder(reminder: RecitationReminder) {
        val current = getReminders().toMutableList()
        val index = current.indexOfFirst { it.id == reminder.id }
        if (index >= 0) {
            current[index] = reminder
        } else {
            current.add(reminder)
        }
        saveReminders(current)
    }

    fun deleteReminder(id: String) {
        val current = getReminders().filter { it.id != id }
        saveReminders(current)
    }

    fun toggleReminder(id: String): Boolean {
        val current = getReminders().toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index >= 0) {
            val updated = current[index].copy(isEnabled = !current[index].isEnabled)
            current[index] = updated
            saveReminders(current)
            return updated.isEnabled
        }
        return false
    }

    // Audio Volumes
    fun getQuranVolume(): Float = prefs.getFloat("vol_quran", 1.0f)
    fun setQuranVolume(v: Float) = prefs.edit().putFloat("vol_quran", v).apply()

    fun getAmbientVolume(): Float = prefs.getFloat("vol_ambient", 0.4f)
    fun setAmbientVolume(v: Float) = prefs.edit().putFloat("vol_ambient", v).apply()

    fun getSelectedAmbientId(): String = prefs.getString("selected_ambient_id", "none") ?: "none"
    fun setSelectedAmbientId(id: String) = prefs.edit().putString("selected_ambient_id", id).apply()

    // Daily Progress
    private fun getTodayDateString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun getDailyProgress(): DailyProgress {
        val today = getTodayDateString()
        val savedDate = prefs.getString("progress_date", today)
        var todayMinutes = prefs.getInt("today_minutes", 8)
        var streak = prefs.getInt("current_streak", 4)

        if (savedDate != today) {
            todayMinutes = 0
            prefs.edit().putString("progress_date", today).putInt("today_minutes", 0).apply()
        }

        val goal = prefs.getInt("daily_goal_minutes", 15)
        val completed = prefs.getInt("completed_surahs_count", 14)

        return DailyProgress(
            todayMinutes = todayMinutes,
            dailyGoalMinutes = goal,
            currentStreak = streak,
            lastListenedDate = today,
            totalSurahsCompleted = completed
        )
    }

    fun incrementDailyTime(minutes: Int) {
        val today = getTodayDateString()
        val savedDate = prefs.getString("progress_date", today)
        var currentMins = if (savedDate == today) prefs.getInt("today_minutes", 0) else 0
        currentMins += minutes
        prefs.edit()
            .putString("progress_date", today)
            .putInt("today_minutes", currentMins)
            .apply()
    }
}
