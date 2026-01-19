package com.example.islamicapp.quran

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

/**
 * Audio cache for Quran recitations.
 *
 * الفكرة: أول ما المستخدم يشغّل السورة أونلاين، ExoPlayer بيحفظ البيانات تلقائياً في الكاش.
 * بعد كده، نفس السورة (بنفس الرابط) تشتغل أوفلاين بدون ما تحس.
 */
@UnstableApi
object QuranAudioCache {
    @Volatile
    private var cache: Cache? = null

    /**
     * Initialize once from Application.
     */
    fun init(context: Context) {
        if (cache != null) return
        synchronized(this) {
            if (cache != null) return
            val cacheDir = File(context.cacheDir, "quran_audio_cache")
            // 2GB LRU cache (تقدر تغيّره لاحقاً لو حبيت)
            val evictor = LeastRecentlyUsedCacheEvictor(2L * 1024L * 1024L * 1024L)
            cache = SimpleCache(cacheDir, evictor)
        }
    }

    fun get(): Cache {
        return cache
            ?: throw IllegalStateException("QuranAudioCache not initialized. Call QuranAudioCache.init(context) in Application.onCreate().")
    }
}
