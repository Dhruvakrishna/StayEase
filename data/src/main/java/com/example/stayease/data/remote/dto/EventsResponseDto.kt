package com.example.stayease.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventsResponseDto(
    @Json(name = "_embedded") val embedded: EmbeddedEventsDto?
)

@JsonClass(generateAdapter = true)
data class EmbeddedEventsDto(
    @Json(name = "events") val events: List<EventDto>
)

@JsonClass(generateAdapter = true)
data class EventDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "url") val url: String?,
    @Json(name = "images") val images: List<ImageDto>?,
    @Json(name = "dates") val dates: DatesDto?,
    @Json(name = "_embedded") val embedded: EventEmbeddedDto?
)

@JsonClass(generateAdapter = true)
data class ImageDto(
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class DatesDto(
    @Json(name = "start") val start: StartDto?
)

@JsonClass(generateAdapter = true)
data class StartDto(
    @Json(name = "localDate") val localDate: String?
)

@JsonClass(generateAdapter = true)
data class EventEmbeddedDto(
    @Json(name = "venues") val venues: List<VenueDto>?
)

@JsonClass(generateAdapter = true)
data class VenueDto(
    @Json(name = "name") val name: String?,
    @Json(name = "location") val location: LocationDto?
)

@JsonClass(generateAdapter = true)
data class LocationDto(
    @Json(name = "latitude") val latitude: String?,
    @Json(name = "longitude") val longitude: String?
)
