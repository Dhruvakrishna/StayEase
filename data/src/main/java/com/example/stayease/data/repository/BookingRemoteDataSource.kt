package com.example.stayease.data.repository
import com.example.stayease.domain.model.Booking
interface BookingRemoteDataSource {
  suspend fun create(booking: Booking): Pair<String, String>
  suspend fun cancel(serverId: String): Boolean
}
