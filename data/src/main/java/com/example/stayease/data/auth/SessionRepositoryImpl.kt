package com.example.stayease.data.auth
import com.example.stayease.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(store: TokenStore) : SessionRepository {
  override val isLoggedIn: Flow<Boolean> = store.tokens.map { it != null }
}
