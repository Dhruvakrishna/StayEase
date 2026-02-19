package com.example.stayease.data.auth
import com.example.stayease.domain.model.AuthTokens
interface AuthProvider {
  suspend fun login(email: String, password: String): AuthTokens
  suspend fun refresh(refreshToken: String): AuthTokens
}
