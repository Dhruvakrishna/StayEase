package com.example.stayease.domain.model

data class CmsContent(
    val banners: List<Banner> = emptyList(),
    val collections: List<HotelCollection> = emptyList()
)

data class Banner(
    val id: String,
    val imageUrl: String,
    val title: String,
    val description: String,
    val actionUrl: String? = null
)

data class HotelCollection(
    val id: String,
    val title: String,
    val subtitle: String,
    val hotelIds: List<Long>
)
