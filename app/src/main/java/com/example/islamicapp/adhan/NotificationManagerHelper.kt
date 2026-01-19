package com.example.islamicapp.adhan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.islamicapp.R
import com.example.islamicapp.settings.AppSettings
import kotlinx.coroutines.flow.first

object NotificationManagerHelper {

    fun createChannelId(type: String, soundName: String): String {
        return "channel_${type}_${soundName}"
    }

    fun getSoundUri(context: Context, soundName: String): Uri? {
        val resId = context.resources.getIdentifier(soundName, "raw", context.packageName)
        return if (resId != 0) {
            Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/$resId")
        } else {
            // Fallback: try "adhan" if the specific one is missing, otherwise default sound
            val fallbackId = context.resources.getIdentifier("adhan", "raw", context.packageName)
            if (fallbackId != 0) {
                Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/$fallbackId")
            } else {
                null // System default will be used if null? No, setSound(null) means silent usually.
                // We should return a valid URI if we want sound.
            }
        }
    }

    fun createChannel(context: Context, type: String, soundName: String, mode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = createChannelId(type, soundName)
            val name = "Islamic App $type Channel"
            // Importance HIGH for Adhan to make sound and pop up
            val importance = if (mode == "SILENT") NotificationManager.IMPORTANCE_LOW else NotificationManager.IMPORTANCE_HIGH
            
            val channel = NotificationChannel(channelId, name, importance)
            
            if (mode != "SILENT") {
                 val soundUri = getSoundUri(context, soundName)
                 if (soundUri != null) {
                     val audioAttributes = AudioAttributes.Builder()
                         .setUsage(AudioAttributes.USAGE_ALARM)
                         .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                         .build()
                     channel.setSound(soundUri, audioAttributes)
                 }
            } else {
                channel.setSound(null, null)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    suspend fun testAdhanNotification(context: Context) {
        val settings = AppSettings.observe(context).first()
        if (!settings.notifAdhanEnabled) return

        val highPrivacy = settings.highPrivacyMode

        val soundName = settings.notifAdhanSound
        val mode = if (settings.quietMode) "SILENT" else settings.notifAdhanMode
        
        createChannel(context, "ADHAN", soundName, mode)

        val title = if (highPrivacy) {
            "تذكير"
        } else {
            "تجربة إشعار الأذان"
        }

        val text = if (highPrivacy) {
            "تنبيه مجدول"
        } else {
            "هذا اختبار لإشعار الأذان ($mode)"
        }
        
        val builder = NotificationCompat.Builder(context, createChannelId("ADHAN", soundName))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        
        try {
            NotificationManagerCompat.from(context).notify(1001, builder.build())
        } catch (e: SecurityException) {
            // Handle missing permission
        }
    }

    suspend fun testReminderNotification(context: Context) {
        val settings = AppSettings.observe(context).first()
        if (!settings.reminderEnabled) return

        val highPrivacy = settings.highPrivacyMode

        // Reminders usually just beep or default sound
        val soundName = "beep_1" 
        val mode = if (settings.quietMode) "SILENT" else "BEEP"

        createChannel(context, "REMINDER", soundName, mode)

        val title = if (highPrivacy) {
            "تذكير"
        } else {
            "تجربة التذكير"
        }

        val text = if (highPrivacy) {
            "تنبيه مجدول"
        } else {
            "اقترب وقت الصلاة ($mode)"
        }

        val builder = NotificationCompat.Builder(context, createChannelId("REMINDER", soundName))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
             .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(1002, builder.build())
        } catch (e: SecurityException) { }
    }

    suspend fun testAzkarNotification(context: Context) {
        val settings = AppSettings.observe(context).first()
        if (!settings.notifAzkarEnabled) return

        val highPrivacy = settings.highPrivacyMode

        val soundName = if (settings.notifAzkarVoice) "dua_1" else "beep_1"
        val mode = if (settings.quietMode) "SILENT" else if (settings.notifAzkarVoice) "DUA" else "BEEP"

        createChannel(context, "AZKAR", soundName, mode)

        val title = if (highPrivacy) {
            "تذكير"
        } else {
            "تجربة الأذكار"
        }

        val text = if (highPrivacy) {
            "تنبيه مجدول"
        } else {
            "لا تنس ذكر الله ($mode)"
        }

        val builder = NotificationCompat.Builder(context, createChannelId("AZKAR", soundName))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
             .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(1003, builder.build())
        } catch (e: SecurityException) { }
    }
    
    // Helper to trigger actual Adhan from Receiver
    suspend fun showAdhanNotification(context: Context, prayerName: String) {
        val settings = AppSettings.observe(context).first()
        if (!settings.notifAdhanEnabled) return

        val highPrivacy = settings.highPrivacyMode

        val soundName = settings.notifAdhanSound
        val mode = if (settings.quietMode) "SILENT" else settings.notifAdhanMode
        
        createChannel(context, "ADHAN", soundName, mode)
        
        val intent = android.content.Intent(context, com.example.islamicapp.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (highPrivacy) {
            "تذكير"
        } else {
            "حان الآن موعد $prayerName"
        }

        val text = if (highPrivacy) {
            "تنبيه مجدول"
        } else {
            "حي على الصلاة، حي على الفلاح"
        }

        val builder = NotificationCompat.Builder(context, createChannelId("ADHAN", soundName))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
        
        try {
            NotificationManagerCompat.from(context).notify(2000, builder.build())
        } catch (e: SecurityException) { }
    }

    suspend fun showReminderNotification(context: Context, prayerName: String) {
        val settings = AppSettings.observe(context).first()
        if (!settings.reminderEnabled) return

        val highPrivacy = settings.highPrivacyMode

        val soundName = "beep_1" // Reminder always uses simple beep for now
        val mode = if (settings.quietMode) "SILENT" else "BEEP"

        createChannel(context, "REMINDER", soundName, mode)

        val intent = android.content.Intent(context, com.example.islamicapp.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (highPrivacy) {
            "تذكير"
        } else {
            "اقترب موعد $prayerName"
        }

        val text = if (highPrivacy) {
            "تنبيه مجدول"
        } else {
            "باقي ${settings.reminderMinutes} دقائق على الصلاة"
        }

        val builder = NotificationCompat.Builder(context, createChannelId("REMINDER", soundName))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(2001, builder.build())
        } catch (e: SecurityException) { }
    }

    suspend fun showAzkarNotification(context: Context, type: String) {
        val settings = AppSettings.observe(context).first()
        if (!settings.notifAzkarEnabled) return

        val highPrivacy = settings.highPrivacyMode

        val soundName = if (settings.notifAzkarVoice) "dua_1" else "beep_1"
        val mode = if (settings.quietMode) "SILENT" else if (settings.notifAzkarVoice) "DUA" else "BEEP"

        createChannel(context, "AZKAR", soundName, mode)

        val title = if (highPrivacy) {
            "تذكير"
        } else {
            when (type) {
                "MORNING" -> "أذكار الصباح"
                "EVENING" -> "أذكار المساء"
                "SLEEP" -> "أذكار النوم"
                else -> "ذكر الله"
            }
        }

        val text = if (highPrivacy) {
            "تنبيه مجدول"
        } else {
            when (type) {
                "MORNING" -> "أصبحنا وأصبح الملك لله"
                "EVENING" -> "أمسينا وأمسى الملك لله"
                "SLEEP" -> "باسمك ربي وضعت جنبي"
                else -> "لا تنس ذكر الله"
            }
        }

        val intent = android.content.Intent(context, com.example.islamicapp.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, createChannelId("AZKAR", soundName))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(2002, builder.build())
        } catch (e: SecurityException) { }
    }

    suspend fun showIntentionReminderNotification(context: Context) {
        val settings = AppSettings.observe(context).first()
        if (!settings.notificationsEnabled) return

        val soundName = "beep_1"
        val mode = if (settings.quietMode) "SILENT" else "BEEP"

        createChannel(context, "INTENTION", soundName, mode)

        val intent = android.content.Intent(context, com.example.islamicapp.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (settings.highPrivacyMode) {
            "تذكير"
        } else {
            "تذكير النية اليومية"
        }

        val text = if (settings.highPrivacyMode) {
            "تنبيه مجدول"
        } else {
            "هل احتسبت نيتك اليوم؟"
        }

        val builder = NotificationCompat.Builder(context, createChannelId("INTENTION", soundName))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(2003, builder.build())
        } catch (e: SecurityException) { }
    }
}
