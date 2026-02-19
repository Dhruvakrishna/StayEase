package com.example.stayease.domain.usecase
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.AuthTokens
import com.example.stayease.domain.repository.AuthRepository
import javax.inject.Inject
class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
  suspend operator fun invoke(email: String, password: String): AppResult<AuthTokens> = repo.login(email, password)
}
