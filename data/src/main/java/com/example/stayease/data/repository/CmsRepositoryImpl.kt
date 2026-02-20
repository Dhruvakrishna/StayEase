package com.example.stayease.data.repository

import com.example.stayease.core.result.AppResult
import com.example.stayease.core.result.safeCall
import com.example.stayease.domain.model.Banner
import com.example.stayease.domain.model.CmsContent
import com.example.stayease.domain.model.HotelCollection
import com.example.stayease.domain.repository.CmsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CmsRepositoryImpl @Inject constructor() : CmsRepository {

    private val content = MutableStateFlow(CmsContent())

    override fun observeContent(): Flow<CmsContent> = content

    override suspend fun refreshContent(): AppResult<Unit> = safeCall {
        // In a real app, this would fetch from a headless CMS like Contentful or Firebase Remote Config
        // Simulating a network fetch
        val mockBanners = listOf(
            Banner(
                id = "1",
                imageUrl = "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1200&q=80",
                title = "Summer Escape",
                description = "Get up to 30% off on beachfront resorts this season."
            ),
            Banner(
                id = "2",
                imageUrl = "https://images.unsplash.com/photo-1517840901100-8179e982ad91?auto=format&fit=crop&w=1200&q=80",
                title = "Urban Luxury",
                description = "Experience the heart of the city in style."
            )
        )

        val mockCollections = listOf(
            HotelCollection(
                id = "c1",
                title = "Most Loved by Travelers",
                subtitle = "Hand-picked stays with perfect ratings.",
                hotelIds = listOf(1, 2, 3)
            )
        )

        content.value = CmsContent(mockBanners, mockCollections)
    }
}
