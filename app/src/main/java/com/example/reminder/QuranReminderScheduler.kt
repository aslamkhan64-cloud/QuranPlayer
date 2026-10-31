package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.model.RecitationReminder
import com.example.data.model.SurahCatalog
import com.example.data.storage.QuranPreferences
import java.util.Calendar

object QuranReminderScheduler {
    const val CHANNEL_ID = "quran_recitation_reminders"
    const val CHANNEL_NAME = "Quran Recitation Reminders"
    const val ACTION_REMINDER = "com.example.ACTION_QURAN_REMINDER"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily reminders to recite and listen to the Holy Quran"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleAll(context: Context) {
        val prefs = QuranPreferences(context)
        val reminders = prefs.getReminders()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        reminders.forEach { reminder ->
            if (reminder.isEnabled) {
                scheduleReminder(context, alarmManager, reminder)
            } else {
                cancelReminder(context, alarmManager, reminder.id)
            }
        }
    }

    fun scheduleReminder(context: Context, alarmManager: AlarmManager, reminder: RecitationReminder) {
        val intent = Intent(context, QuranReminderReceiver::class.java).apply {
            action = ACTION_REMINDER
            putExtra("reminder_id", reminder.id)
            putExtra("reminder_title", reminder.title)
            putExtra("surah_number", reminder.targetSurahNumber ?: -1)
        }

        val requestCode = reminder.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelReminder(context: Context, alarmManager: AlarmManager, reminderId: String) {
        val intent = Intent(context, QuranReminderReceiver::class.java).apply {
            action = ACTION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun triggerTestNotification(context: Context, reminder: RecitationReminder) {
        createNotificationChannel(context)
        val targetSurah = reminder.targetSurahNumber?.let { SurahCatalog.getByNumber(it) }
        val surahInfo = if (targetSurah != null) {
            "Time to recite ${targetSurah.name} (${targetSurah.arabicName})"
        } else {
            "Take a peaceful pause for your daily Quran recitation."
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("play_surah_num", reminder.targetSurahNumber ?: 1)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("📖 ${reminder.title}")
            .setContentText(surahInfo)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$surahInfo\n\"Verily in the remembrance of Allah do hearts find rest.\""))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(reminder.id.hashCode(), builder.build())
    }
}
