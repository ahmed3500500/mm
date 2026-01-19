package com.example.islamicapp.adhan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object AdhanScheduler {
    const val ACTION_ADHAN = "com.example.islamicapp.action.ADHAN"
    const val ACTION_REMINDER = "com.example.islamicapp.action.REMINDER"
    const val ACTION_AZKAR = "com.example.islamicapp.action.AZKAR"
    const val ACTION_INTENTION_REMINDER = "com.example.islamicapp.action.INTENTION_REMINDER"

    private fun getPendingIntent(context: Context, action: String, id: Int, extras: Map<String, String> = emptyMap()): PendingIntent {
        val intent = Intent(context, AdhanReceiver::class.java).apply {
            this.action = action
            extras.forEach { (k, v) -> putExtra(k, v) }
        }
        return PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun scheduleAdhan(context: Context, triggerAtMillis: Long, prayerName: String) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pending = getPendingIntent(context, ACTION_ADHAN, prayerName.hashCode(), mapOf("prayer_name" to prayerName))
        scheduleAlarm(manager, triggerAtMillis, pending)
    }

    fun scheduleReminder(context: Context, triggerAtMillis: Long, prayerName: String) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pending = getPendingIntent(context, ACTION_REMINDER, prayerName.hashCode() + 1000, mapOf("prayer_name" to prayerName))
        scheduleAlarm(manager, triggerAtMillis, pending)
    }

    fun scheduleAzkar(context: Context, triggerAtMillis: Long, type: String) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pending = getPendingIntent(context, ACTION_AZKAR, type.hashCode(), mapOf("azkar_type" to type))
        scheduleAlarm(manager, triggerAtMillis, pending)
    }

    fun scheduleDailyIntentionReminder(context: Context) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 21)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }
        val pending = getPendingIntent(
            context,
            ACTION_INTENTION_REMINDER,
            99999,
            emptyMap()
        )
        scheduleAlarm(manager, calendar.timeInMillis, pending)
    }

    private fun scheduleAlarm(manager: AlarmManager, triggerAtMillis: Long, pending: PendingIntent) {
        if (triggerAtMillis <= System.currentTimeMillis()) return 
        
        try {
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pending
            )
        } catch (e: SecurityException) {
            manager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pending
            )
        }
    }

    fun schedulePrayers(
        context: Context,
        state: com.example.islamicapp.prayer.PrayerTimesUiState,
        settings: com.example.islamicapp.settings.SettingsState
    ) {
        val prayers = mapOf(
            "الفجر" to state.fajr,
            "الظهر" to state.dhuhr,
            "العصر" to state.asr,
            "المغرب" to state.maghrib,
            "العشاء" to state.isha
        )

        val now = System.currentTimeMillis()

        prayers.forEach { (name, timeStr) ->
            if (timeStr.isNotEmpty()) {
                val timeMillis = parseTime(timeStr)
                if (timeMillis > now) {
                    if (settings.adhanEnabled && settings.notifAdhanEnabled) {
                        scheduleAdhan(context, timeMillis, name)
                    }
                    if (settings.reminderEnabled) {
                        val reminderTime = timeMillis - (settings.reminderMinutes * 60 * 1000)
                        if (reminderTime > now) {
                            scheduleReminder(context, reminderTime, name)
                        }
                    }
                }
            }
        }

        // Azkar
        if (settings.notifAzkarEnabled) {
            // Morning: Sunrise + delay
            if (state.sunrise.isNotEmpty()) {
                val sunriseMillis = parseTime(state.sunrise)
                val morningTime = sunriseMillis + (settings.azkarMorningDelay * 60 * 1000)
                if (morningTime > now) {
                    scheduleAzkar(context, morningTime, "MORNING")
                }
            }

            // Evening: Asr + delay
            if (state.asr.isNotEmpty()) {
                val asrMillis = parseTime(state.asr)
                val eveningTime = asrMillis + (settings.azkarEveningDelay * 60 * 1000)
                if (eveningTime > now) {
                    scheduleAzkar(context, eveningTime, "EVENING")
                }
            }

            // Sleep: fixed time
            if (settings.azkarSleepTime.isNotEmpty()) {
                val sleepMillis = parseTime(settings.azkarSleepTime)
                if (sleepMillis > now) {
                    scheduleAzkar(context, sleepMillis, "SLEEP")
                }
            }
        }
    }

    private fun parseTime(timeStr: String): Long {
        try {
            val cleanTime = timeStr.substringBefore(" ")
            val parts = cleanTime.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
            calendar.set(java.util.Calendar.MINUTE, minute)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        } catch (e: Exception) {
            return 0L
        }
    }
}
