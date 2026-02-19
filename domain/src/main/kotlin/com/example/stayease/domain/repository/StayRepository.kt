package com.example.stayease.domain.repository
import androidx.paging.PagingData
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.Stay
import kotlinx.coroutines.flow.Flow

interface StayRepository {
  fun staysNear(pivot: GeoPoint, radiusMeters: Int, pageSize: Int): Flow<PagingData<Stay>>
  suspend fun getStayDetails(id: Long): AppResult<Stay>
  suspend fun refreshCache(pivot: GeoPoint, radiusMeters: Int, limit: Int = 40): AppResult<Unit>
}
