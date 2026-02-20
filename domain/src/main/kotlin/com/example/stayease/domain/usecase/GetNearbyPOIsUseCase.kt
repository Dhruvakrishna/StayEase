package com.example.stayease.domain.usecase

import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.PointOfInterest
import com.example.stayease.domain.repository.PointOfInterestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNearbyPOIsUseCase @Inject constructor(
    private val repository: PointOfInterestRepository
) {
    operator fun invoke(
        location: GeoPoint,
        category: String = "all",
        radiusMeters: Int = 1000
    ): Flow<List<PointOfInterest>> {
        return repository.getNearbyPointsOfInterest(location, category, radiusMeters)
    }
}
