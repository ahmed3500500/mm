package com.example.islamicapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuranDao {

    @Query("SELECT COUNT(*) FROM quran_ayah")
    suspend fun countQuranAyahs(): Int

    @Query("SELECT * FROM quran_ayah WHERE surah = :surah ORDER BY ayah ASC")
    suspend fun getAyahsForSurah(surah: Int): List<QuranAyahEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAyahs(items: List<QuranAyahEntity>)

    @Query("SELECT COUNT(*) FROM tafsir_ayah WHERE sourceKey = :sourceKey")
    suspend fun countTafsirAyahs(sourceKey: String): Int

    @Query("SELECT * FROM tafsir_ayah WHERE sourceKey = :sourceKey AND surah = :surah ORDER BY ayah ASC")
    suspend fun getTafsirForSurah(sourceKey: String, surah: Int): List<TafsirAyahEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTafsir(items: List<TafsirAyahEntity>)

    @Query("SELECT * FROM quran_ayah WHERE text LIKE '%' || :text || '%' ORDER BY surah ASC, ayah ASC")
    suspend fun searchAyahsByText(text: String): List<QuranAyahEntity>
}
