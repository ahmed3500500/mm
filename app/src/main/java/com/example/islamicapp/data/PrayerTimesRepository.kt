package com.example.islamicapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object PrayerTimesRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.aladhan.com/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val api = retrofit.create(AlAdhanService::class.java)

    suspend fun getTimingsByCity(city: String, country: String): PrayerTimesResponse {
        return withContext(Dispatchers.IO) {
            api.getTimingsByCity(city, country)
        }
    }

    suspend fun getTimingsByCoordinates(latitude: Double, longitude: Double): PrayerTimesResponse {
        return withContext(Dispatchers.IO) {
            api.getTimingsByCoordinates(latitude, longitude)
        }
    }
}
