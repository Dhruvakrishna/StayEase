package com.example.stayease.domain.model

data class PointOfInterest(
    val id: String,
    val name: String,
    val category: String,
    val rating: Double?,
    val location: GeoPoint,
    val distance: Double?, // Distance from the stay in meters
    val tags: List<String> = emptyList(),
    val imageUrl: String? = null
)
