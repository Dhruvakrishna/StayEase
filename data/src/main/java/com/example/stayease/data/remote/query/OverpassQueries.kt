package com.example.stayease.data.remote.query
import com.example.stayease.domain.model.GeoPoint

fun staysAround(pivot: GeoPoint, radiusMeters: Int): String {
  val lat = pivot.lat
  val lon = pivot.lon
  return """
    [out:json][timeout:25];
    (
      nwr["tourism"="hotel"](around:$radiusMeters,$lat,$lon);
      nwr["tourism"="hostel"](around:$radiusMeters,$lat,$lon);
      nwr["tourism"="guest_house"](around:$radiusMeters,$lat,$lon);
      nwr["tourism"="apartment"](around:$radiusMeters,$lat,$lon);
      nwr["tourism"="motel"](around:$radiusMeters,$lat,$lon);
    );
    out center;
  """.trimIndent()
}
