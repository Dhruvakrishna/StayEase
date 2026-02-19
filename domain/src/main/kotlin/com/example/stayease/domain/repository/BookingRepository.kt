package com.example.stayease.domain.repository
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.Booking
import com.example.stayease.domain.model.BookingStatus
import com.example.stayease.domain.model.Stay
import kotlinx.coroutines.flow.Flow

data class SyncState(val lastSyncEpochMs: Long?, val inProgress: Boolean, val lastError: String?)

interface BookingRepository {
  fun observeBookings(): Flow<List<Booking>>
  fun observeSyncState(): Flow<SyncState>

  suspend fun createLocalBooking(
    stay: Stay,
    checkInEpochDay: Long,
    checkOutEpochDay: Long,
    guests: Int,
    rooms: Int,
    roomType: String,
    specialRequests: String?
  ): AppResult<Long>

  suspend fun cancelBooking(localId: Long): AppResult<Unit>
  suspend fun syncPending(): AppResult<Unit>
  suspend fun updateStatus(localId: Long, status: BookingStatus, confirmationCode: String? = null): AppResult<Unit>
}
