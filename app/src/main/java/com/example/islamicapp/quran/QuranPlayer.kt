package com.example.islamicapp.quran

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource

enum class Reciter(val displayName: String, val baseUrl: String, val extraPath: String = "") {
    AL_AFASY("مشاري العفاسي", "https://server8.mp3quran.net/afs/"),
    SAAD_GHAMDI("سعد الغامدي", "https://server7.mp3quran.net/s_gmd/"),
    ABDUL_BASIT("عبد الباسط عبد الصمد", "https://server7.mp3quran.net/basit/"),
    MAHER("ماهر المعيقلي", "https://server12.mp3quran.net/maher/"),
    SUDAYS("عبد الرحمن السديس", "https://server11.mp3quran.net/sds/"),
    SHURAIM("سعود الشريم", "https://server7.mp3quran.net/shur/"),
    AJMI("أحمد العجمي", "https://server10.mp3quran.net/ajm/", "128/")
}

/**
 * QuranPlayer (Online first, then Offline automatically)
 *
 * الفكرة اللي طلبتها بالضبط:
 * - السورة تتشغّل أونلاين أول مرة.
 * - أثناء التشغيل ExoPlayer بيكتب البيانات في الكاش.
 * - بعد ما تسمعها مرة، نفس السورة تشتغل أوفلاين لاحقاً (طالما الكاش ما اتمسح).
 */
@UnstableApi
object QuranPlayer {
    private var player: ExoPlayer? = null
    var currentReciter: Reciter = Reciter.AL_AFASY
        private set

    var onSurahCompleted: ((surahNumber: Int) -> Unit)? = null
    private var currentSurahNumber: Int? = null

    fun setReciter(reciter: Reciter) {
        currentReciter = reciter
    }

    fun playSurah(context: Context, surahNumber: Int) {
        ensurePlayer(context.applicationContext)
        currentSurahNumber = surahNumber

        val url = buildUrlForSurah(surahNumber)
        val mediaItem = MediaItem.fromUri(url)

        val httpFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        val upstreamFactory = DefaultDataSource.Factory(context.applicationContext, httpFactory)

        val cacheFactory = CacheDataSource.Factory()
            .setCache(QuranAudioCache.get())
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSource = ProgressiveMediaSource.Factory(cacheFactory)
            .createMediaSource(mediaItem)

        player?.apply {
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }
    }

    fun stop() {
        player?.stop()
        currentSurahNumber = null
    }

    fun release() {
        player?.release()
        player = null
        currentSurahNumber = null
    }

    private fun ensurePlayer(appContext: Context) {
        if (player != null) return

        // Ensure cache is initialized (in case Application didn't run for some reason)
        QuranAudioCache.init(appContext)

        val p = ExoPlayer.Builder(appContext).build()
        p.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    val done = currentSurahNumber
                    currentSurahNumber = null
                    if (done != null) onSurahCompleted?.invoke(done)
                }
            }
        })
        player = p
    }

    private fun buildUrlForSurah(number: Int): String {
        val formatted = number.toString().padStart(3, '0')
        return currentReciter.baseUrl + currentReciter.extraPath + "$formatted.mp3"
    }
}
