package com.example.stayease.data.auth
import com.example.stayease.domain.model.AuthTokens
import kotlin.random.Random

class DemoAuthProvider : AuthProvider {
  override suspend fun login(email: String, password: String): AuthTokens {
    val now = System.currentTimeMillis()
    return AuthTokens("demo.jwt.${Random.nextInt()}", "demo.refresh.${Random.nextInt()}", now + 60_000)
  }
  override suspend fun refresh(refreshToken: String): AuthTokens {
    val now = System.currentTimeMillis()
    return AuthTokens("demo.jwt.${Random.nextInt()}", refreshToken, now + 60_000)
  }
}
