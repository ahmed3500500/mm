package com.example.islamicapp.data.net

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuranEncTranslationMeta(
    val key: String,
    val language_iso_code: String,
    val version: String? = null,
    val last_update: Long? = null,
    val title: String? = null,
    val description: String? = null
)

@JsonClass(generateAdapter = true)
data class QuranEncAyahItem(
    val sura: Int,
    val aya: Int,
    val translation: String,
    val footnotes: String? = null
)

@JsonClass(generateAdapter = true)
data class QuranEncSuraResponse(
    val result: List<QuranEncAyahItem>
)
