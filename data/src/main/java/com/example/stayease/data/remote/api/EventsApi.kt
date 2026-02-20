package com.example.stayease.data.remote.api

import com.example.stayease.data.remote.dto.EventsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface EventsApi {
    @GET("api/v1/events")
    suspend fun getEvents(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("category") category: String?,
        @Query("radius") radius: Int = 20000
    ): EventsResponseDto
}
