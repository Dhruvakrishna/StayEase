package com.example.stayease.domain.usecase
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.repository.StayRepository
import javax.inject.Inject
class RefreshStaysUseCase @Inject constructor(private val repo: StayRepository) {
  suspend operator fun invoke(pivot: GeoPoint, radiusMeters: Int = 2500, limit: Int = 40): AppResult<Unit> =
    repo.refreshCache(pivot, radiusMeters, limit)
}
