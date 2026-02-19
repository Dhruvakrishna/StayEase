package com.example.stayease.data.remote.api
import com.example.stayease.data.remote.dto.OverpassResponseDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OverpassApi {
  @FormUrlEncoded
  @POST("api/interpreter")
  suspend fun query(@Field("data") query: String): OverpassResponseDto
}
