package com.example.stayease.core.domain.repository

import com.example.stayease.core.domain.model.Trip
import com.example.stayease.core.domain.model.ItineraryItem
import com.example.stayease.core.result.AppResult
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getTrips(): Flow<List<Trip>>
    fun getTripById(id: String): Flow<Trip?>
    suspend fun saveTrip(trip: Trip): AppResult<Unit>
    suspend fun deleteTrip(id: String): AppResult<Unit>
    
    fun getItinerary(tripId: String): Flow<List<ItineraryItem>>
    suspend fun saveItineraryItem(item: ItineraryItem): AppResult<Unit>
    suspend fun deleteItineraryItem(id: String): AppResult<Unit>
    
    suspend fun syncPendingChanges(): AppResult<Unit>
}
