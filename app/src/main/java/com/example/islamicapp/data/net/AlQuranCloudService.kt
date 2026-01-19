package com.example.islamicapp.data.net

import retrofit2.http.GET
import retrofit2.http.Path

interface AlQuranCloudService {

    // Returns Arabic text by default (quran-uthmani) when edition is omitted.
    @GET("/v1/surah/{surah}")
    suspend fun getSurah(@Path("surah") surah: Int): AlQuranCloudResponse<SurahPayload>
}
