package com.example.stayease.domain.model
enum class BookingStatus { PENDING, CONFIRMED, CANCELLED, FAILED }

data class Booking(
  val localId: Long,
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
  val status: BookingStatus,
  val confirmationCode: String?,
  val createdAtEpochMs: Long,
  val updatedAtEpochMs: Long
)
