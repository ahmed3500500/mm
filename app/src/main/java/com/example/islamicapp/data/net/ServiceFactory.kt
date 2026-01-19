package com.example.islamicapp.data.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ServiceFactory {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun retrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val alQuranCloud: AlQuranCloudService by lazy {
        retrofit("https://api.alquran.cloud/")
            .create(AlQuranCloudService::class.java)
    }

    val quranEnc: QuranEncService by lazy {
        retrofit("https://quranenc.com/")
            .create(QuranEncService::class.java)
    }
}
