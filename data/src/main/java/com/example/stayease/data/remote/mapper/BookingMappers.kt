package com.example.stayease.data.remote.mapper
import com.example.stayease.data.remote.dto.CreateBookingRequestDto
import com.example.stayease.domain.model.Booking

fun Booking.toCreateRequest(): CreateBookingRequestDto = CreateBookingRequestDto(
  stayId = stayId,
  stayName = stayName,
  checkInEpochDay = checkInEpochDay,
  checkOutEpochDay = checkOutEpochDay,
  guests = guests,
  rooms = rooms,
  roomType = roomType,
  specialRequests = specialRequests,
  currency = currency
)
