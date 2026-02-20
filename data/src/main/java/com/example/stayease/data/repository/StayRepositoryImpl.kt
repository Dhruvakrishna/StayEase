package com.example.stayease.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.stayease.core.result.AppResult
import com.example.stayease.core.result.safeCall
import com.example.stayease.data.local.AppDatabase
import com.example.stayease.data.local.entity.StayEntity
import com.example.stayease.data.remote.api.OverpassApi
import com.example.stayease.data.remote.mapper.toStay
import com.example.stayease.data.remote.query.staysAround
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.repository.StayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StayRepositoryImpl @Inject constructor(
    private val api: OverpassApi,
    private val db: AppDatabase
) : StayRepository {

    private val dao = db.stayDao()

    // Cache last pivot and radius to avoid redundant calls
    private var lastPivot: GeoPoint? = null
    private var lastRadius: Int = 0

    @OptIn(ExperimentalPagingApi::class)
    override fun staysNear(pivot: GeoPoint, radiusMeters: Int, pageSize: Int): Flow<PagingData<Stay>> {
        // Update cache tracking
        lastPivot = pivot
        lastRadius = radiusMeters

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = pageSize * 2,
                prefetchDistance = pageSize
            ),
            remoteMediator = StaysRemoteMediator(api, db, pivot, radiusMeters),
            pagingSourceFactory = { dao.pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getStayDetails(id: Long): AppResult<Stay> = safeCall {
        val cached = dao.getStay(id)
        if (cached != null && cached.imageUrls.isNotEmpty()) {
            return@safeCall cached.toDomain()
        }
        
        // Only refresh if we have location info
        val pivot = lastPivot ?: GeoPoint(41.8781, -87.6298)
        val radius = lastRadius.takeIf { it > 0 } ?: 2500

        refreshCache(pivot, radius, 40)
        val after = dao.getStay(id) ?: throw IllegalStateException("Stay not found even after refresh")
        after.toDomain()
    }

    override suspend fun refreshCache(pivot: GeoPoint, radiusMeters: Int, limit: Int): AppResult<Unit> = safeCall {
        val res = api.query(staysAround(pivot, radiusMeters))
        val stays = res.elements.mapNotNull { it.toStay() }.distinctBy { it.id }.take(limit)
        val entities = stays.map { it.toEntity() }
        dao.upsertAll(entities)
    }

    override suspend fun toggleFavorite(id: Long): AppResult<Unit> = safeCall {
        dao.toggleFavorite(id)
    }

    override fun observeFavorites(): Flow<List<Stay>> =
        dao.observeFavorites().map { entities -> entities.map { it.toDomain() } }

    private fun StayEntity.toDomain() = Stay(
        id, name, category, rating, address, GeoPoint(lat, lon),
        nightlyPriceUsdEstimate, thumbnailUrl, imageUrls, amenities, reviews, isFavorite, description
    )

    private fun Stay.toEntity() = StayEntity(
        id, name, category, rating, address, location.lat, location.lon,
        nightlyPriceUsdEstimate, thumbnailUrl, imageUrls, amenities, reviews, isFavorite, description
    )
}
