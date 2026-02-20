package com.example.stayease.domain.model

data class TravelEvent(
    val id: String,
    val name: String,
    val description: String?,
    val category: String, // nature, sports, music, social
    val location: GeoPoint,
    val imageUrl: String?,
    val eventDate: String?,
    val venueName: String?,
    val ticketUrl: String? = null
)
