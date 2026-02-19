package com.example.stayease.domain.repository
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.AuthTokens
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
  val tokens: Flow<AuthTokens?>
  suspend fun login(email: String, password: String): AppResult<AuthTokens>
  suspend fun ensureValidTokens(): AppResult<AuthTokens>
  suspend fun forceRefresh(): AppResult<AuthTokens>
  suspend fun logout(): AppResult<Unit>
}
