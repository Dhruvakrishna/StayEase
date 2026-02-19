package com.example.stayease.data.repository
import com.example.stayease.data.remote.api.BookingApi
import com.example.stayease.data.remote.mapper.toCreateRequest
import com.example.stayease.domain.model.Booking
import javax.inject.Inject

class BookingRemoteDataSourceImpl @Inject constructor(private val api: BookingApi) : BookingRemoteDataSource {
  override suspend fun create(booking: Booking): Pair<String, String> {
    val res = api.createBooking(booking.toCreateRequest())
    return res.bookingId to res.confirmationCode
  }
  override suspend fun cancel(serverId: String): Boolean { api.cancel(serverId); return true }
}
