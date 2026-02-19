package com.example.stayease.data.remote.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OverpassResponseDto(val elements: List<OverpassElementDto>)

@JsonClass(generateAdapter = true)
data class OverpassElementDto(
  val type: String,
  val id: Long,
  val lat: Double? = null,
  val lon: Double? = null,
  val center: OverpassCenterDto? = null,
  val tags: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class OverpassCenterDto(val lat: Double, val lon: Double)
