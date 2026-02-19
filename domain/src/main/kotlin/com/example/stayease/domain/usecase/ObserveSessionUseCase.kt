package com.example.stayease.domain.usecase
import com.example.stayease.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
class ObserveSessionUseCase @Inject constructor(private val repo: SessionRepository) {
  operator fun invoke(): Flow<Boolean> = repo.isLoggedIn
}
