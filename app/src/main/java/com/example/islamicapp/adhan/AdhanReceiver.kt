package com.example.islamicapp.adhan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdhanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        val action = intent?.action ?: return
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                when (action) {
                    AdhanScheduler.ACTION_ADHAN -> {
                        val prayerName = intent.getStringExtra("prayer_name") ?: "الصلاة"
                        NotificationManagerHelper.showAdhanNotification(context, prayerName)
                    }
                    AdhanScheduler.ACTION_REMINDER -> {
                        val prayerName = intent.getStringExtra("prayer_name") ?: "الصلاة"
                        NotificationManagerHelper.showReminderNotification(context, prayerName)
                    }
                    AdhanScheduler.ACTION_AZKAR -> {
                        val type = intent.getStringExtra("azkar_type") ?: "GENERAL"
                        NotificationManagerHelper.showAzkarNotification(context, type)
                    }
                    AdhanScheduler.ACTION_INTENTION_REMINDER -> {
                        NotificationManagerHelper.showIntentionReminderNotification(context)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
