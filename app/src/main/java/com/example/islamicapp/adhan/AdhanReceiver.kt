package com.example.islamicapp.adhan

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.islamicapp.MainActivity
import com.example.islamicapp.R

class AdhanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val player = MediaPlayer.create(context, R.raw.adhan)
        player.setOnCompletionListener {
            it.release()
        }
        player.start()

        val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "prayer_adhan_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("حان الآن وقت الصلاة")
            .setContentText("اللهم اجعلنا من المحافظين على الصلاة في أوقاتها")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(2001, notification)
    }
}
