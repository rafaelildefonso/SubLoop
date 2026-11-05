package com.subloop.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {
    @GET("latest")
    suspend fun latest(
        @Query("from") from: String = "USD",
        @Query("to") to: String = "BRL",
    ): ExchangeRateResponse
}

@Serializable
data class ExchangeRateResponse(
    @SerialName("base") val base: String,
    @SerialName("date") val date: String,
    @SerialName("rates") val rates: Map<String, Double>,
)
