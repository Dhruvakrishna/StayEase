package com.example.stayease.domain.repository

import com.example.stayease.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun observeLocation(): Flow<GeoPoint?>
    suspend fun getCurrentLocation(): GeoPoint?
}
