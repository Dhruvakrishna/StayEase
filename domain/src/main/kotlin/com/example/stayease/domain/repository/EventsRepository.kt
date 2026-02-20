package com.example.stayease.domain.repository

import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.TravelEvent
import kotlinx.coroutines.flow.Flow

interface EventsRepository {
    fun getEvents(location: GeoPoint, category: String, radiusMeters: Int = 20000): Flow<List<TravelEvent>>
}
