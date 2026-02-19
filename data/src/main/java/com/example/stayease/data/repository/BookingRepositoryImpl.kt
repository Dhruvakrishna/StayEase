package com.example.stayease.data.repository
import com.example.stayease.core.result.AppResult
import com.example.stayease.core.result.safeCall
import com.example.stayease.core.telemetry.Telemetry
import com.example.stayease.data.local.dao.BookingDao
import com.example.stayease.data.local.entity.BookingEntity
import com.example.stayease.domain.model.*
import com.example.stayease.domain.repository.BookingRepository
import com.example.stayease.domain.repository.SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.max

class BookingRepositoryImpl @Inject constructor(
  private val dao: BookingDao,
  private val remote: BookingRemoteDataSource,
  private val telemetry: Telemetry
) : BookingRepository {

  private val syncState = MutableStateFlow(SyncState(lastSyncEpochMs = null, inProgress = false, lastError = null))
  override fun observeSyncState(): Flow<SyncState> = syncState

  override fun observeBookings(): Flow<List<Booking>> =
    dao.observeBookings().map { it.map { e -> e.toDomain() } }

  override suspend fun createLocalBooking(
    stay: Stay,
    checkInEpochDay: Long,
    checkOutEpochDay: Long,
    guests: Int,
    rooms: Int,
    roomType: String,
    specialRequests: String?
  ): AppResult<Long> = safeCall {
    val nights = max(1, (checkOutEpochDay - checkInEpochDay).toInt())
    val total = stay.nightlyPriceUsdEstimate.toDouble() * nights * rooms
    val now = System.currentTimeMillis()
    val entity = BookingEntity(
      serverId = null,
      stayId = stay.id,
      stayName = stay.name,
      checkInEpochDay = checkInEpochDay,
      checkOutEpochDay = checkOutEpochDay,
      guests = guests,
      rooms = rooms,
      roomType = roomType,
      specialRequests = specialRequests,
      currency = "USD",
      totalAmount = total,
      status = BookingStatus.PENDING.name,
      confirmationCode = null,
      createdAtEpochMs = now,
      updatedAtEpochMs = now
    )
    val id = dao.insert(entity)
    telemetry.logEvent("booking_created_local", mapOf("stayId" to stay.id, "rooms" to rooms, "guests" to guests))
    id
  }

  override suspend fun cancelBooking(localId: Long): AppResult<Unit> = safeCall {
    val cur = dao.get(localId) ?: return@safeCall
    dao.update(cur.copy(status = BookingStatus.CANCELLED.name, updatedAtEpochMs = System.currentTimeMillis()))
    cur.serverId?.let { remote.cancel(it) }
    telemetry.logEvent("booking_cancelled", mapOf("localId" to localId))
  }

  override suspend fun syncPending(): AppResult<Unit> = safeCall {
    syncState.value = syncState.value.copy(inProgress = true, lastError = null)
    try {
      val pending = dao.pendingOrFailed()
      for (e in pending) {
        val (serverId, confirmation) = remote.create(e.toDomain())
        dao.update(e.copy(serverId = serverId, confirmationCode = confirmation, status = BookingStatus.CONFIRMED.name, updatedAtEpochMs = System.currentTimeMillis()))
        telemetry.logEvent("booking_synced", mapOf("localId" to e.localId))
      }
      syncState.value = SyncState(lastSyncEpochMs = System.currentTimeMillis(), inProgress = false, lastError = null)
    } catch (t: Throwable) {
      telemetry.recordNonFatal(t, mapOf("stage" to "syncPending"))
      syncState.value = SyncState(lastSyncEpochMs = syncState.value.lastSyncEpochMs, inProgress = false, lastError = t.message)
      throw t
    }
  }

  override suspend fun updateStatus(localId: Long, status: BookingStatus, confirmationCode: String?): AppResult<Unit> = safeCall {
    val cur = dao.get(localId) ?: return@safeCall
    dao.update(cur.copy(status = status.name, confirmationCode = confirmationCode ?: cur.confirmationCode, updatedAtEpochMs = System.currentTimeMillis()))
  }

  private fun BookingEntity.toDomain(): Booking = Booking(
    localId = localId,
    serverId = serverId,
    stayId = stayId,
    stayName = stayName,
    checkInEpochDay = checkInEpochDay,
    checkOutEpochDay = checkOutEpochDay,
    guests = guests,
    rooms = rooms,
    roomType = roomType,
    specialRequests = specialRequests,
    currency = currency,
    totalAmount = totalAmount,
    status = BookingStatus.valueOf(status),
    confirmationCode = confirmationCode,
    createdAtEpochMs = createdAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs
  )
}
