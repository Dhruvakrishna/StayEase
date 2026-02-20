package com.example.stayease.domain.usecase

import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.repository.StayRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(private val repo: StayRepository) {
    operator fun invoke(): Flow<List<Stay>> = repo.observeFavorites()
}
