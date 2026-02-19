package com.example.stayease.domain.usecase
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.repository.BookingRepository
import javax.inject.Inject
class SyncBookingsUseCase @Inject constructor(private val repo: BookingRepository) {
  suspend operator fun invoke(): AppResult<Unit> = repo.syncPending()
}
