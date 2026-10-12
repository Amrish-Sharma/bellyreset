package com.codebuzz.app.bellyreset.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object ReminderManager {

    private const val DAILY_REMINDER_ID = 1001
    private const val WEEKLY_REMINDER_ID = 1002

    fun scheduleReminders(context: Context) {
        scheduleDailyReminder(context)
        scheduleWeeklyReminder(context)
    }

    fun cancelReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        listOf(DAILY_REMINDER_ID, WEEKLY_REMINDER_ID).forEach { id ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                Intent(context, ReminderReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }

    private fun scheduleDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", "Belly Reset")
            putExtra("EXTRA_MESSAGE", "Close out your day.")
            putExtra("EXTRA_ID", DAILY_REMINDER_ID)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun scheduleWeeklyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", "Belly Reset")
            putExtra("EXTRA_MESSAGE", "Waist + weight check.")
            putExtra("EXTRA_ID", WEEKLY_REMINDER_ID)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            WEEKLY_REMINDER_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }
}
