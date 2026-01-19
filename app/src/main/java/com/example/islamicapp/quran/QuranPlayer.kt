package com.example.islamicapp.quran

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

object QuranPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun playSurah(context: Context, surahNumber: Int) {
        stop()
        val url = buildUrlForSurah(surahNumber)
        val player = MediaPlayer()
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        player.setDataSource(url)
        player.setOnPreparedListener { it.start() }
        player.setOnCompletionListener {
            stop()
        }
        mediaPlayer = player
        player.prepareAsync()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun buildUrlForSurah(number: Int): String {
        val formatted = number.toString().padStart(3, '0')
        return "https://server8.mp3quran.net/afs/$formatted.mp3"
    }
}

