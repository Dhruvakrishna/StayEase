package com.example.stayease.data.remote.mapper

import com.example.stayease.data.remote.dto.OverpassElementDto
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.Review
import com.example.stayease.domain.model.Stay
import kotlin.math.max

fun OverpassElementDto.toStay(): Stay? {
    val tags = tags ?: return null
    val name = tags["name"] ?: return null
    val category = tags["tourism"] ?: tags["amenity"] ?: "stay"
    val point = when {
        lat != null && lon != null -> GeoPoint(lat, lon)
        center != null -> GeoPoint(center.lat, center.lon)
        else -> return null
    }
    val addressParts = listOfNotNull(
        tags["addr:housenumber"],
        tags["addr:street"],
        tags["addr:city"],
        tags["addr:state"],
        tags["addr:postcode"]
    )
    val address = addressParts.joinToString(" ").ifBlank { null }
    val rating = tags["stars"]?.toDoubleOrNull()?.coerceIn(0.0, 5.0)
    val nightly = max(89, 110 + (id % 120).toInt())

    // Enhanced with rich data (Fake for now as Overpass doesn't provide these)
    val thumb = "https://images.unsplash.com/photo-${1566073771259 + (id % 1000)}-6a8506099945?auto=format&fit=crop&w=400&q=80"
    val images = listOf(
        "https://images.unsplash.com/photo-${1566073771259 + (id % 1000)}-6a8506099945?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-${1520250497591 + (id % 1000)}-d607ac2b1a60?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-${1551882547 + (id % 1000)}-d24c0b21c45e?auto=format&fit=crop&w=800&q=80"
    )

    val allAmenities = listOf("Free Wi-Fi", "Pool", "Fitness Center", "Spa", "Restaurant", "Bar", "Parking", "Room Service")
    val selectedAmenities = allAmenities.filterIndexed { index, _ -> (id + index) % 3 == 0L }.take(5)

    val reviews = listOf(
        Review("Alex J.", 4.5, "Amazing view and very clean rooms. Highly recommend!"),
        Review("Maria S.", 5.0, "The staff was incredibly helpful and the breakfast was delicious."),
        Review("John D.", 3.5, "Decent stay, but the room was a bit smaller than expected.")
    )

    return Stay(
        id = id,
        name = name,
        category = category,
        rating = rating,
        address = address,
        location = point,
        nightlyPriceUsdEstimate = nightly,
        thumbnailUrl = thumb,
        imageUrls = images,
        amenities = selectedAmenities,
        reviews = reviews
    )
}
