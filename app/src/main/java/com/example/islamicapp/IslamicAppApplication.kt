package com.example.islamicapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.islamicapp.quran.QuranAudioCache

class IslamicAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Quran audio cache (stream online -> auto cache -> play offline later)
        QuranAudioCache.init(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "prayer_adhan_channel",
                "أذان الصلاة",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}

