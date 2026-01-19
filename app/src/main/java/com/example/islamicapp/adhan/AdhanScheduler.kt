package com.example.islamicapp.adhan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object AdhanScheduler {
    fun scheduleNextAdhan(context: Context, diffMinutes: Int) {
        if (diffMinutes <= 0) return
        val triggerAtMillis = System.currentTimeMillis() + diffMinutes * 60_000L
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AdhanReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pending
            )
        } catch (e: SecurityException) {
            // Fallback for Android 12+ if permission is missing
            manager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pending
            )
        }
    }
}

