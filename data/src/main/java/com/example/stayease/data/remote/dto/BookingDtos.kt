package com.example.stayease.data.remote.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateBookingRequestDto(
  val stayId: Long,
  val stayName: String,
  val checkInEpochDay: Long,
  val checkOutEpochDay: Long,
  val guests: Int,
  val rooms: Int,
  val roomType: String,
  val specialRequests: String?,
  val currency: String
)

@JsonClass(generateAdapter = true)
data class CreateBookingResponseDto(
  val bookingId: String,
  val confirmationCode: String,
  val status: String,
  val totalAmount: Double,
  val currency: String,
  val createdAtEpochMs: Long
)

@JsonClass(generateAdapter = true)
data class CancelBookingResponseDto(val bookingId: String, val status: String)

@JsonClass(generateAdapter = true)
data class BookingItemDto(
  val bookingId: String,
  val stayId: Long,
  val stayName: String,
  val status: String,
  val totalAmount: Double,
  val currency: String,
  val confirmationCode: String?,
  val createdAtEpochMs: Long
)

@JsonClass(generateAdapter = true)
data class BookingListResponseDto(val items: List<BookingItemDto>)
