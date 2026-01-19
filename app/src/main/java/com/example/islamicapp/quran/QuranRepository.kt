package com.example.islamicapp.quran

import android.content.Context
import com.example.islamicapp.data.db.QuranAyahEntity
import com.example.islamicapp.data.db.QuranDatabase
import com.example.islamicapp.data.db.TafsirAyahEntity
import com.example.islamicapp.data.net.ServiceFactory

class QuranRepository(private val context: Context) {

    private val db = QuranDatabase.get(context)
    private val dao = db.dao()

    suspend fun isQuranAvailableOffline(): Boolean {
        // Quran contains 6236 ayahs. Using > 2000 as a safe threshold for "downloaded".
        return dao.countQuranAyahs() > 2000
    }

    suspend fun getSurahAyahs(surah: Int): List<QuranAyahEntity> {
        return dao.getAyahsForSurah(surah)
    }

    suspend fun getTafsirForSurah(sourceKey: String, surah: Int): List<TafsirAyahEntity> {
        return dao.getTafsirForSurah(sourceKey, surah)
    }

    suspend fun chooseDefaultArabicTafsirKey(): Pair<String, String> {
        // Heuristic: prefer "التفسير الميسر", then "السعدي", then any Arabic option.
        val list = ServiceFactory.quranEnc.listTranslations(language = "ar", localization = "ar")
        val normalized = list.map { meta ->
            val title = meta.title.orEmpty()
            meta.key to title
        }
        val byPriority = normalized.sortedWith(
            compareBy<Pair<String, String>> { (_, title) ->
                when {
                    title.contains("الميسر") -> 0
                    title.contains("السعدي") -> 1
                    title.contains("مختصر") -> 2
                    else -> 3
                }
            }.thenBy { it.second }
        )
        return byPriority.firstOrNull() ?: ("arabic_mokhtasar" to "تفسير مختصر")
    }

    suspend fun downloadQuranText(onProgress: (surah: Int) -> Unit) {
        // Download surah-by-surah from AlQuranCloud API.
        for (surah in 1..114) {
            val payload = ServiceFactory.alQuranCloud.getSurah(surah).data
            val items = payload.ayahs.map { a ->
                QuranAyahEntity(surah = surah, ayah = a.numberInSurah, text = a.text)
            }
            dao.upsertAyahs(items)
            onProgress(surah)
        }
    }

    suspend fun downloadTafsir(sourceKey: String, sourceTitle: String, onProgress: (surah: Int) -> Unit) {
        for (surah in 1..114) {
            val ayahs = ServiceFactory.quranEnc.getSurahTranslation(sourceKey, surah)
            val items = ayahs.map { a ->
                TafsirAyahEntity(
                    sourceKey = sourceKey,
                    sourceTitle = sourceTitle,
                    surah = surah,
                    ayah = a.aya,
                    text = a.translation
                )
            }
            dao.upsertTafsir(items)
            onProgress(surah)
        }
    }

    suspend fun searchAyahs(text: String): List<QuranAyahEntity> {
        if (text.isBlank()) return emptyList()
        return dao.searchAyahsByText(text)
    }
}
