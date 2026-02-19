package com.example.stayease.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
  @PrimaryKey(autoGenerate = true) val localId: Long = 0,
  val serverId: String?,
  val stayId: Long,
  val stayName: String,
  val checkInEpochDay: Long,
  val checkOutEpochDay: Long,
  val guests: Int,
  val rooms: Int,
  val roomType: String,
  val specialRequests: String?,
  val currency: String,
  val totalAmount: Double,
  val status: String,
  val confirmationCode: String?,
  val createdAtEpochMs: Long,
  val updatedAtEpochMs: Long
)
