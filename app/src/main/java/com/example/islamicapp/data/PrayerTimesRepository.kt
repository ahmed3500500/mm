package com.example.islamicapp.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object PrayerTimesRepository {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.aladhan.com/v1/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(AlAdhanService::class.java)

    suspend fun getTimingsByCity(city: String, country: String): PrayerTimesResponse {
        return withContext(Dispatchers.IO) {
            api.getTimingsByCity(city, country)
        }
    }

    suspend fun getTimingsByCoordinates(latitude: Double, longitude: Double): PrayerTimesResponse {
        val date = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.US).format(java.util.Date())
        return withContext(Dispatchers.IO) {
            // method = null means auto-detect based on location/country by API
            api.getTimingsByCoordinates(date, latitude, longitude, method = null)
        }
    }
}
