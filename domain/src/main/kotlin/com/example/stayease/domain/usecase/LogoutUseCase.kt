package com.example.stayease.domain.usecase
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.repository.AuthRepository
import javax.inject.Inject
class LogoutUseCase @Inject constructor(private val repo: AuthRepository) {
  suspend operator fun invoke(): AppResult<Unit> = repo.logout()
}
