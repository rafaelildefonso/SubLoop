package com.subloop.network

import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

object NetworkModule {
    private val json = Json { ignoreUnknownKeys = true }

    val exchangeRateApi: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.frankfurter.app/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ExchangeRateApi::class.java)
    }
}
