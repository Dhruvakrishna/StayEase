package com.example.stayease.data.repository

import com.example.stayease.data.remote.api.OverpassApi
import com.example.stayease.data.remote.api.WikimediaApi
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.PointOfInterest
import com.example.stayease.domain.repository.PointOfInterestRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PointOfInterestRepositoryImpl @Inject constructor(
    private val api: OverpassApi,
    private val wikimediaApi: WikimediaApi
) : PointOfInterestRepository {

    override fun getNearbyPointsOfInterest(location: GeoPoint, category: String, radiusMeters: Int): Flow<List<PointOfInterest>> = flow {
        // Optimization: Server-side filtering using Overpass QL
        val categoryFilter = when (category) {
            "Nature" -> """node["leisure"~"park|nature_reserve"](around:$radiusMeters,${location.lat},${location.lon});
                           node["tourism"~"viewpoint"](around:$radiusMeters,${location.lat},${location.lon});"""
            "Music" -> """node["amenity"~"theatre|arts_centre|nightclub"](around:$radiusMeters,${location.lat},${location.lon});"""
            "Events" -> """node["amenity"~"museum|arts_centre|community_centre"](around:$radiusMeters,${location.lat},${location.lon});
                            node["tourism"~"gallery|museum"](around:$radiusMeters,${location.lat},${location.lon});"""
            "Sports" -> """node["leisure"~"stadium|pitch|sports_centre"](around:$radiusMeters,${location.lat},${location.lon});"""
            "Social" -> """node["amenity"~"cafe|restaurant|bar|pub"](around:$radiusMeters,${location.lat},${location.lon});"""
            else -> """node["tourism"](around:$radiusMeters,${location.lat},${location.lon});
                       node["amenity"](around:$radiusMeters,${location.lat},${location.lon});"""
        }

        val query = """
            [out:json];
            (
              $categoryFilter
            );
            out body;
        """.trimIndent()

        try {
            val response = api.query(query)
            val pois = coroutineScope {
                response.elements
                    .filter { it.tags?.get("name") != null }
                    .take(15) // Reduced to 15 for even faster loading
                    .map { element ->
                        async {
                            val name = element.tags!!["name"]!!
                            val lat = element.lat ?: 0.0
                            val lon = element.lon ?: 0.0
                            
                            var imageUrl: String? = null
                            try {
                                val wikiSearch = wikimediaApi.searchImage("$lat|$lon")
                                val pageTitle = wikiSearch.query?.geosearch?.firstOrNull()?.title
                                if (pageTitle != null) {
                                    val imageInfo = wikimediaApi.getImageInfo(pageTitle)
                                    imageUrl = imageInfo.query?.pages?.values?.firstOrNull()?.imageinfo?.firstOrNull()?.url
                                }
                            } catch (e: Exception) {}

                            PointOfInterest(
                                id = element.id.toString(),
                                name = name,
                                category = element.tags["tourism"] ?: element.tags["amenity"] ?: element.tags["leisure"] ?: "Interest",
                                rating = element.tags["rating"]?.toDoubleOrNull(),
                                location = GeoPoint(lat, lon),
                                distance = null,
                                tags = element.tags.keys.toList(),
                                imageUrl = imageUrl
                            )
                        }
                    }.awaitAll()
            }
            emit(pois)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
