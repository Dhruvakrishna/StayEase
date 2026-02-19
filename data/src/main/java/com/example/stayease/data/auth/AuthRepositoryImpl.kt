package com.example.stayease.data.auth
import com.example.stayease.core.result.AppError
import com.example.stayease.core.result.AppResult
import com.example.stayease.core.result.safeCall
import com.example.stayease.domain.model.AuthTokens
import com.example.stayease.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
  private val store: TokenStore,
  private val provider: AuthProvider
) : AuthRepository {

  override val tokens: Flow<AuthTokens?> = store.tokens

  override suspend fun login(email: String, password: String): AppResult<AuthTokens> {
    if (email.isBlank() || password.isBlank()) return AppResult.Err(AppError.Validation("Email and password required"))
    return safeCall { provider.login(email, password).also { store.save(it) } }
  }

  override suspend fun ensureValidTokens(): AppResult<AuthTokens> = safeCall {
    val cur = store.tokens.first()
    val now = System.currentTimeMillis()
    when {
      cur == null -> provider.login("guest@stayease.com", "guest").also { store.save(it) }
      cur.expiresAtEpochMs <= now -> provider.refresh(cur.refreshToken).also { store.save(it) }
      else -> cur
    }
  }

  override suspend fun forceRefresh(): AppResult<AuthTokens> = safeCall {
    val cur = store.tokens.first() ?: provider.login("guest@stayease.com", "guest")
    provider.refresh(cur.refreshToken).also { store.save(it) }
  }

  override suspend fun logout(): AppResult<Unit> = safeCall { store.clear() }
}
