package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.storage.QuranPreferences

class QuranReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all on device boot
            QuranReminderScheduler.scheduleAll(context)
            return
        }

        if (intent.action == QuranReminderScheduler.ACTION_REMINDER) {
            val reminderId = intent.getStringExtra("reminder_id") ?: return
            val prefs = QuranPreferences(context)
            val reminder = prefs.getReminders().firstOrNull { it.id == reminderId }
            if (reminder != null && reminder.isEnabled) {
                QuranReminderScheduler.triggerTestNotification(context, reminder)
            }
        }
    }
}
