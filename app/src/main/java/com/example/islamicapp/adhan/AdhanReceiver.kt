package com.example.islamicapp.adhan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.islamicapp.R

class AdhanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val player = MediaPlayer.create(context, R.raw.adhan)
        player.setOnCompletionListener {
            it.release()
        }
        player.start()

        val notification = NotificationCompat.Builder(context, "prayer_adhan_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("حان الآن وقت الصلاة")
            .setContentText("اللهم اجعلنا من المحافظين على الصلاة في أوقاتها")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(2001, notification)
    }
}
