package com.example.stayease.domain.model

data class Stay(
    val id: Long,
    val name: String,
    val category: String,
    val rating: Double?,
    val address: String?,
    val location: GeoPoint,
    val nightlyPriceUsdEstimate: Int,
    val thumbnailUrl: String? = null,
    val imageUrls: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val isFavorite: Boolean = false,
    val description: String? = null
)

data class Review(
    val author: String,
    val rating: Double,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
