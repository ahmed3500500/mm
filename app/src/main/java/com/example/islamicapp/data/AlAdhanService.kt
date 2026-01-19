package com.example.islamicapp.data

import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Query

interface AlAdhanService {
    @GET("timingsByCity")
    suspend fun getTimingsByCity(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int = 2
    ): PrayerTimesResponse

    @GET("timings/{date}")
    suspend fun getTimingsByCoordinates(
        @retrofit2.http.Path("date") date: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int? = null
    ): PrayerTimesResponse
}

data class PrayerTimesResponse(
    val data: PrayerData?
)

data class PrayerData(
    val date: PrayerDate?,
    val timings: PrayerTimings?
)

data class PrayerDate(
    val hijri: HijriDate?
)

data class HijriDate(
    val date: String?
)

data class PrayerTimings(
    @Json(name = "Fajr") val fajr: String?,
    @Json(name = "Sunrise") val sunrise: String?,
    @Json(name = "Dhuhr") val dhuhr: String?,
    @Json(name = "Asr") val asr: String?,
    @Json(name = "Maghrib") val maghrib: String?,
    @Json(name = "Isha") val isha: String?
)
