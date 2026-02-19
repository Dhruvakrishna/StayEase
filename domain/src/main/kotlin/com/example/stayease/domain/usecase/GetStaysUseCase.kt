package com.example.stayease.domain.usecase
import androidx.paging.PagingData
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.repository.StayRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
class GetStaysUseCase @Inject constructor(private val repo: StayRepository) {
  operator fun invoke(pivot: GeoPoint, radiusMeters: Int = 2500, pageSize: Int = 20): Flow<PagingData<Stay>> =
    repo.staysNear(pivot, radiusMeters, pageSize)
}
