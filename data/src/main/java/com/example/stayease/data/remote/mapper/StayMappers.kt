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
    val rating = tags["stars"]?.toDoubleOrNull()?.coerceIn(0.0, 5.0) ?: (3.5 + (id % 15) / 10.0)
    val nightly = max(89, 110 + (id % 120).toInt())

    // Use source.unsplash.com for more reliable thematic images
    val categoryTerm = if (category.contains("hotel", ignoreCase = true)) "hotel" else "resort"
    val thumb = "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=400&q=80" // High quality fallback
    
    // Rotating through a set of high-quality verified hotel images to ensure they always load
    val imagePool = listOf(
        "1566073771259-6a8506099945",
        "1520250497591-d607ac2b1a60",
        "1551882547-d24c0b21c45e",
        "1584132967334-10e028bd69f7",
        "1542314831-068cd1dbfeeb",
        "1571896349842-33c89424de2d"
    )
    
    val imageId = imagePool[(id % imagePool.size).toInt()]
    val finalThumb = "https://images.unsplash.com/photo-$imageId?auto=format&fit=crop&w=400&q=80"
    val images = imagePool.map { "https://images.unsplash.com/photo-$it?auto=format&fit=crop&w=800&q=80" }

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
        thumbnailUrl = finalThumb,
        imageUrls = images,
        amenities = selectedAmenities,
        reviews = reviews
    )
}
