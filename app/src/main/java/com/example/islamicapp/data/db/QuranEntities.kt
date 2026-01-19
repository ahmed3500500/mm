package com.example.islamicapp.data.db

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "quran_ayah",
    primaryKeys = ["surah", "ayah"],
    indices = [Index("surah"), Index("ayah")]
)
data class QuranAyahEntity(
    val surah: Int,
    val ayah: Int,
    val text: String
)

@Entity(
    tableName = "tafsir_ayah",
    primaryKeys = ["sourceKey", "surah", "ayah"],
    indices = [Index("sourceKey"), Index("surah"), Index("ayah")]
)
data class TafsirAyahEntity(
    val sourceKey: String,
    val sourceTitle: String,
    val surah: Int,
    val ayah: Int,
    val text: String
)
