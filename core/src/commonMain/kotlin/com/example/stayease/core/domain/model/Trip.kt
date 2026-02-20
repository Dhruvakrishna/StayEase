package com.example.stayease.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    val id: String,
    val title: String,
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val description: String = ""
)

@Serializable
data class ItineraryItem(
    val id: String,
    val tripId: String,
    val title: String,
    val startTime: Long,
    val location: String,
    val notes: String = "",
    val type: ItineraryType = ItineraryType.ACTIVITY
)

@Serializable
enum class ItineraryType {
    FLIGHT, HOTEL, ACTIVITY, TRANSPORT, DINING
}
