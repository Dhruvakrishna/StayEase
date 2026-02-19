package com.example.stayease.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.stayease.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
  @Insert suspend fun insert(entity: BookingEntity): Long
  @Update suspend fun update(entity: BookingEntity)

  @Query("SELECT * FROM bookings ORDER BY createdAtEpochMs DESC")
  fun observeBookings(): Flow<List<BookingEntity>>

  @Query("SELECT * FROM bookings WHERE status IN ('PENDING','FAILED') ORDER BY createdAtEpochMs ASC")
  suspend fun pendingOrFailed(): List<BookingEntity>

  @Query("SELECT * FROM bookings WHERE localId = :id LIMIT 1")
  suspend fun get(id: Long): BookingEntity?
}
