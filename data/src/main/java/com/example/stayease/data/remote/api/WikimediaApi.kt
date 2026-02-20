package com.example.stayease.data.remote.api

import com.example.stayease.data.remote.dto.WikimediaImageInfoResponseDto
import com.example.stayease.data.remote.dto.WikimediaResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WikimediaApi {
    @GET("api.php?action=query&list=geosearch&gsradius=10000&gslimit=1&format=json")
    suspend fun searchImage(
        @Query("gscoord") coordinates: String
    ): WikimediaResponseDto

    @GET("api.php?action=query&prop=imageinfo&iiprop=url&format=json")
    suspend fun getImageInfo(
        @Query("titles") titles: String
    ): WikimediaImageInfoResponseDto
}
