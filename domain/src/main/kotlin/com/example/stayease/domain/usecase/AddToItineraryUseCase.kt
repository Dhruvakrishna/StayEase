package com.example.stayease.domain.usecase

import com.example.stayease.core.domain.model.ItineraryItem
import com.example.stayease.core.domain.model.ItineraryType
import com.example.stayease.core.domain.repository.TripRepository
import com.example.stayease.domain.model.PointOfInterest
import java.util.UUID
import javax.inject.Inject

class AddToItineraryUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(tripId: String, poi: PointOfInterest) {
        val item = ItineraryItem(
            id = UUID.randomUUID().toString(),
            tripId = tripId,
            title = poi.name,
            startTime = System.currentTimeMillis(),
            location = "${poi.location.lat},${poi.location.lon}",
            notes = "Added from nearby attractions: ${poi.category}",
            type = ItineraryType.ACTIVITY
        )
        repository.saveItineraryItem(item)
    }
}
