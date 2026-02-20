package com.example.stayease.data.local.dao

import androidx.room.*
import com.example.stayease.data.local.entity.TripEntity
import com.example.stayease.data.local.entity.ItineraryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun getTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :id")
    fun getTripById(id: String): Flow<TripEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun deleteTrip(id: String)

    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId ORDER BY startTime ASC")
    fun getItinerary(tripId: String): Flow<List<ItineraryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryItem(item: ItineraryEntity)

    @Query("DELETE FROM itinerary_items WHERE id = :id")
    suspend fun deleteItineraryItem(id: String)

    @Query("SELECT * FROM trips WHERE isPendingSync = 1")
    suspend fun getPendingTrips(): List<TripEntity>

    @Query("SELECT * FROM itinerary_items WHERE isPendingSync = 1")
    suspend fun getPendingItineraryItems(): List<ItineraryEntity>
}
