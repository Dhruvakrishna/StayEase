package com.example.stayease.data.repository

import com.example.stayease.core.domain.model.Trip
import com.example.stayease.core.domain.model.ItineraryItem
import com.example.stayease.core.domain.repository.TripRepository
import com.example.stayease.core.result.AppResult
import com.example.stayease.core.result.AppError
import com.example.stayease.data.local.dao.TripDao
import com.example.stayease.data.local.entity.toDomain
import com.example.stayease.data.local.entity.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao
) : TripRepository {

    override fun getTrips(): Flow<List<Trip>> {
        return tripDao.getTrips().map { entities -> 
            entities.map { it.toDomain() } 
        }
    }

    override fun getTripById(id: String): Flow<Trip?> {
        return tripDao.getTripById(id).map { it?.toDomain() }
    }

    override suspend fun saveTrip(trip: Trip): AppResult<Unit> {
        return try {
            tripDao.insertTrip(trip.toEntity(isPendingSync = true))
            AppResult.Ok(Unit)
        } catch (e: Exception) {
            AppResult.Err(AppError.Unexpected(e.message ?: "Error saving trip"))
        }
    }

    override suspend fun deleteTrip(id: String): AppResult<Unit> {
        return try {
            tripDao.deleteTrip(id)
            AppResult.Ok(Unit)
        } catch (e: Exception) {
            AppResult.Err(AppError.Unexpected(e.message ?: "Error deleting trip"))
        }
    }

    override fun getItinerary(tripId: String): Flow<List<ItineraryItem>> {
        return tripDao.getItinerary(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveItineraryItem(item: ItineraryItem): AppResult<Unit> {
        return try {
            tripDao.insertItineraryItem(item.toEntity(isPendingSync = true))
            AppResult.Ok(Unit)
        } catch (e: Exception) {
            AppResult.Err(AppError.Unexpected(e.message ?: "Error saving itinerary item"))
        }
    }

    override suspend fun deleteItineraryItem(id: String): AppResult<Unit> {
        return try {
            tripDao.deleteItineraryItem(id)
            AppResult.Ok(Unit)
        } catch (e: Exception) {
            AppResult.Err(AppError.Unexpected(e.message ?: "Error deleting itinerary item"))
        }
    }

    override suspend fun syncPendingChanges(): AppResult<Unit> {
        // In a real implementation, this would fetch pending items from DB
        // and upload them via an API client.
        val pendingTrips = tripDao.getPendingTrips()
        val pendingItems = tripDao.getPendingItineraryItems()
        
        // Mocking successful sync for now
        return AppResult.Ok(Unit)
    }
}
