package com.codebuzz.app.bellyreset.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.codebuzz.app.bellyreset.data.repository.TrackerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val appContext = context.applicationContext
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val settings = TrackerRepository(appContext).trackerState.first().settings
                    if (settings.remindersEnabled) {
                        ReminderManager.scheduleReminders(appContext)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
