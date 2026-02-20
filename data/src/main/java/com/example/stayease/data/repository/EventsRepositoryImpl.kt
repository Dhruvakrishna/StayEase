package com.example.stayease.data.repository

import com.example.stayease.data.remote.api.EventsApi
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.TravelEvent
import com.example.stayease.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EventsRepositoryImpl @Inject constructor(
    private val api: EventsApi
) : EventsRepository {

    override fun getEvents(location: GeoPoint, category: String, radiusMeters: Int): Flow<List<TravelEvent>> = flow {
        try {
            // Map our vibes to Ticketmaster segments/classifications
            val classificationName = when (category) {
                "Music" -> "Music"
                "Sports" -> "Sports"
                "Events" -> "Arts & Theatre"
                "Social" -> "Miscellaneous"
                else -> null
            }

            val latlong = "${location.lat},${location.lon}"
            val radius = (radiusMeters / 1609).coerceIn(1, 100) // Ticketmaster uses miles

            // In a real app, we'd use a BuildConfig field for the API key if needed here.
            // For this demo, the key is handled in the Retrofit interceptor in AppModule.
            val response = api.getEvents(
                lat = location.lat,
                lon = location.lon,
                category = classificationName,
                radius = radius
            )

            val events = response.embedded?.events?.map { dto ->
                TravelEvent(
                    id = dto.id,
                    name = dto.name,
                    description = dto.description,
                    category = category,
                    location = GeoPoint(
                        dto.embedded?.venues?.firstOrNull()?.location?.latitude?.toDoubleOrNull() ?: location.lat,
                        dto.embedded?.venues?.firstOrNull()?.location?.longitude?.toDoubleOrNull() ?: location.lon
                    ),
                    imageUrl = dto.images?.firstOrNull()?.url,
                    eventDate = dto.dates?.start?.localDate,
                    venueName = dto.embedded?.venues?.firstOrNull()?.name,
                    ticketUrl = dto.url
                )
            } ?: emptyList()

            emit(events)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
