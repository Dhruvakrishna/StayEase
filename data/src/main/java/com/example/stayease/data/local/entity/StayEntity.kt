package com.example.stayease.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.stayease.domain.model.Review

@Entity(
    tableName = "stays",
    indices = [
        Index("rating"),
        Index("nightlyPriceUsdEstimate"),
        Index("isFavorite"),
        Index("category")
    ]
)
data class StayEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val category: String,
    val rating: Double?,
    val address: String?,
    val lat: Double,
    val lon: Double,
    val nightlyPriceUsdEstimate: Int,
    val thumbnailUrl: String? = null,
    val imageUrls: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val isFavorite: Boolean = false,
    val description: String? = null
)
