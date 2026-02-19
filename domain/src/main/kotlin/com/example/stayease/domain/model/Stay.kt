package com.example.stayease.domain.model
data class Stay(
  val id: Long,
  val name: String,
  val category: String,
  val rating: Double?,
  val address: String?,
  val location: GeoPoint,
  val nightlyPriceUsdEstimate: Int,
  val thumbnailUrl: String? = null
)
