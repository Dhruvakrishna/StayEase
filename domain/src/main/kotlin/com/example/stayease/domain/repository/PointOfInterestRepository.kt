package com.example.stayease.domain.repository

import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.PointOfInterest
import kotlinx.coroutines.flow.Flow

interface PointOfInterestRepository {
    fun getNearbyPointsOfInterest(location: GeoPoint, category: String, radiusMeters: Int): Flow<List<PointOfInterest>>
}
