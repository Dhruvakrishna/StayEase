package com.example.stayease.domain.usecase
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.repository.BookingRepository
import javax.inject.Inject
class CreateBookingUseCase @Inject constructor(private val repo: BookingRepository) {
  suspend operator fun invoke(
    stay: Stay,
    checkInEpochDay: Long,
    checkOutEpochDay: Long,
    guests: Int,
    rooms: Int,
    roomType: String,
    specialRequests: String?
  ): AppResult<Long> =
    repo.createLocalBooking(stay, checkInEpochDay, checkOutEpochDay, guests, rooms, roomType, specialRequests)
}
