package com.example.stayease.domain.usecase

import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.repository.StayRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(private val repo: StayRepository) {
    suspend operator fun invoke(id: Long): AppResult<Unit> = repo.toggleFavorite(id)
}
