package com.example.stayease.domain.repository

import kotlinx.coroutines.flow.Flow

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val hasCompletedOnboarding: Boolean = false
)

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun setTheme(theme: AppTheme)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setOnboardingCompleted(completed: Boolean)
}
