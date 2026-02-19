package com.example.stayease.domain.usecase
import com.example.stayease.domain.model.Booking
import com.example.stayease.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
class GetBookingsUseCase @Inject constructor(private val repo: BookingRepository) {
  operator fun invoke(): Flow<List<Booking>> = repo.observeBookings()
}
