package com.example.islamicapp.data.net

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlQuranCloudResponse<T>(
    val code: Int,
    val status: String,
    val data: T
)

@JsonClass(generateAdapter = true)
data class SurahPayload(
    val number: Int,
    @Json(name = "englishName") val englishName: String? = null,
    @Json(name = "name") val arabicName: String? = null,
    val ayahs: List<AyahPayload>
)

@JsonClass(generateAdapter = true)
data class AyahPayload(
    val numberInSurah: Int,
    val text: String
)
