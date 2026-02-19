package com.example.stayease.domain.model
data class AuthTokens(val accessToken: String, val refreshToken: String, val expiresAtEpochMs: Long)
