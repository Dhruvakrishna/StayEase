package com.example.stayease.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.stayease.core.result.AppResult
import com.example.stayease.core.result.safeCall
import com.example.stayease.data.local.AppDatabase
import com.example.stayease.data.local.dao.StayDao
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

    @OptIn(ExperimentalPagingApi::class)
    override fun staysNear(pivot: GeoPoint, radiusMeters: Int, pageSize: Int): Flow<PagingData<Stay>> {
        // Feature: Offline-First Strategy
        // We use RemoteMediator to ensure that the local database is the Single Source of Truth.
        // The UI observes the DB, and the Mediator handles fetching from network when needed.
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = pageSize
            ),
            remoteMediator = StaysRemoteMediator(api, db, pivot, radiusMeters),
            pagingSourceFactory = { dao.pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                Stay(
                    entity.id, entity.name, entity.category, entity.rating,
                    entity.address, GeoPoint(entity.lat, entity.lon),
                    entity.nightlyPriceUsdEstimate
                )
            }
        }
    }

    override suspend fun getStayDetails(id: Long): AppResult<Stay> = safeCall {
        // Check cache first - classic offline-first move.
        val cached = dao.getStay(id)
        if (cached != null) {
            return@safeCall Stay(
                cached.id, cached.name, cached.category, cached.rating,
                cached.address, GeoPoint(cached.lat, cached.lon),
                cached.nightlyPriceUsdEstimate
            )
        }
        
        // If not found, we fetch and update cache.
        // In a real app, this might be triggered by the Mediator, but here we provide a direct path.
        refreshCache(GeoPoint(41.8781, -87.6298), 2500, 40)
        val after = dao.getStay(id) ?: throw IllegalStateException("Stay not found even after refresh")
        Stay(
            after.id, after.name, after.category, after.rating,
            after.address, GeoPoint(after.lat, after.lon),
            after.nightlyPriceUsdEstimate
        )
    }

    override suspend fun refreshCache(pivot: GeoPoint, radiusMeters: Int, limit: Int): AppResult<Unit> = safeCall {
        val res = api.query(staysAround(pivot, radiusMeters))
        val stays = res.elements.mapNotNull { it.toStay() }.distinctBy { it.id }.take(limit)
        val entities = stays.map { s ->
            StayEntity(
                s.id, s.name, s.category, s.rating, s.address,
                s.location.lat, s.location.lon, s.nightlyPriceUsdEstimate
            )
        }
        db.stayDao().upsertAll(entities)
    }
}
