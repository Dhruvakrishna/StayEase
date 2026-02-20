package com.example.stayease.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WikimediaResponseDto(
    @Json(name = "query") val query: WikimediaQueryDto?
)

@JsonClass(generateAdapter = true)
data class WikimediaQueryDto(
    @Json(name = "geosearch") val geosearch: List<WikimediaGeosearchDto>?
)

@JsonClass(generateAdapter = true)
data class WikimediaGeosearchDto(
    @Json(name = "pageid") val pageid: Long,
    @Json(name = "title") val title: String
)

@JsonClass(generateAdapter = true)
data class WikimediaImageInfoResponseDto(
    @Json(name = "query") val query: WikimediaImageQueryDto?
)

@JsonClass(generateAdapter = true)
data class WikimediaImageQueryDto(
    @Json(name = "pages") val pages: Map<String, WikimediaImagePageDto>?
)

@JsonClass(generateAdapter = true)
data class WikimediaImagePageDto(
    @Json(name = "imageinfo") val imageinfo: List<WikimediaImageInfoDto>?
)

@JsonClass(generateAdapter = true)
data class WikimediaImageInfoDto(
    @Json(name = "url") val url: String
)
