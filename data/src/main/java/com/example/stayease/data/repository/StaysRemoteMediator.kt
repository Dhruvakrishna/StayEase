package com.example.stayease.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.stayease.data.local.AppDatabase
import com.example.stayease.data.local.entity.StayEntity
import com.example.stayease.data.local.entity.StayRemoteKeyEntity
import com.example.stayease.data.remote.api.OverpassApi
import com.example.stayease.data.remote.mapper.toStay
import com.example.stayease.data.remote.query.staysAround
import com.example.stayease.domain.model.GeoPoint

@OptIn(ExperimentalPagingApi::class)
class StaysRemoteMediator(
    private val api: OverpassApi,
    private val db: AppDatabase,
    private val pivot: GeoPoint,
    private val radiusMeters: Int
) : RemoteMediator<Int, StayEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StayEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    nextKey
                }
            }

            // Note: Overpass API doesn't support traditional paging well, 
            // so we're simulating it or fetching a larger batch for now.
            val response = api.query(staysAround(pivot, radiusMeters))
            val stays = response.elements.mapNotNull { it.toStay() }
            
            val endOfPaginationReached = stays.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.stayRemoteKeyDao().clearRemoteKeys()
                    db.stayDao().clear()
                }
                
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = stays.map {
                    StayRemoteKeyEntity(stayId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                
                db.stayRemoteKeyDao().insertAll(keys)
                db.stayDao().upsertAll(stays.map { s ->
                    StayEntity(
                        s.id, s.name, s.category, s.rating, s.address, 
                        s.location.lat, s.location.lon, s.nightlyPriceUsdEstimate
                    )
                })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StayEntity>): StayRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { stay ->
            db.stayRemoteKeyDao().remoteKeysStayId(stay.id)
        }
    }
}
