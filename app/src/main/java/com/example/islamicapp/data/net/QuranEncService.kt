package com.example.islamicapp.data.net

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QuranEncService {

    @GET("/api/v1/translations/list/{language}")
    suspend fun listTranslations(
        @Path("language") language: String,
        @Query("localization") localization: String = "ar"
    ): List<QuranEncTranslationMeta>

    @GET("/api/v1/translation/sura/{key}/{surah}")
    suspend fun getSurahTranslation(
        @Path("key") key: String,
        @Path("surah") surah: Int
    ): List<QuranEncAyahItem>
}
