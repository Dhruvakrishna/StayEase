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

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StayEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
            }

            // Only fetch from API on first load or refresh, reuse cached data for subsequent pages
            val shouldFetchFromApi = loadType == LoadType.REFRESH || state.pages.isEmpty()
            val stays = if (shouldFetchFromApi) {
                val response = api.query(staysAround(pivot, radiusMeters))
                response.elements.mapNotNull { it.toStay() }.distinctBy { it.id }
            } else {
                emptyList()
            }

            val endOfPaginationReached = stays.isEmpty() || page > 1

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.stayRemoteKeyDao().clearRemoteKeys()
                    db.stayDao().clearNonFavorites()
                }
                
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = stays.map {
                    StayRemoteKeyEntity(stayId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                
                if (keys.isNotEmpty()) {
                    db.stayRemoteKeyDao().insertAll(keys)
                    db.stayDao().upsertAll(stays.map { s ->
                        StayEntity(
                            id = s.id,
                            name = s.name,
                            category = s.category,
                            rating = s.rating,
                            address = s.address,
                            lat = s.location.lat,
                            lon = s.location.lon,
                            nightlyPriceUsdEstimate = s.nightlyPriceUsdEstimate,
                            thumbnailUrl = s.thumbnailUrl,
                            imageUrls = s.imageUrls,
                            amenities = s.amenities,
                            reviews = s.reviews,
                            isFavorite = s.isFavorite,
                            description = s.description
                        )
                    })
                }
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StayEntity>): StayRemoteKeyEntity? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id?.let { id ->
                db.stayRemoteKeyDao().remoteKeysStayId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StayEntity>): StayRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { stay ->
            db.stayRemoteKeyDao().remoteKeysStayId(stay.id)
        }
    }
}
