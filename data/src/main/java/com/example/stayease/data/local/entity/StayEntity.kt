package com.example.stayease.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stays")
data class StayEntity(
  @PrimaryKey val id: Long,
  val name: String,
  val category: String,
  val rating: Double?,
  val address: String?,
  val lat: Double,
  val lon: Double,
  val nightlyPriceUsdEstimate: Int
)
