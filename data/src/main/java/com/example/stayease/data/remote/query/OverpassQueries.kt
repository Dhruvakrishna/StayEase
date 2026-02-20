package com.example.stayease.data.remote.query
import com.example.stayease.domain.model.GeoPoint

fun staysAround(pivot: GeoPoint, radiusMeters: Int): String {
  val lat = pivot.lat
  val lon = pivot.lon
  return """
    [out:json][timeout:10];
    (
      nwr["tourism"~"hotel|hostel|guest_house|apartment|motel"](around:$radiusMeters,$lat,$lon);
    );
    out center;
  """.trimIndent()
}
