package com.example.stayease.data.remote.mapper
import com.example.stayease.data.remote.dto.OverpassElementDto
import com.example.stayease.domain.model.GeoPoint
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
  return Stay(id, name, category, rating, address, point, nightly)
}
