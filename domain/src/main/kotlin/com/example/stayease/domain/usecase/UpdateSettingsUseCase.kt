package com.example.stayease.domain.usecase

import com.example.stayease.domain.repository.AppTheme
import com.example.stayease.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(private val repo: SettingsRepository) {
    suspend fun setTheme(theme: AppTheme) = repo.setTheme(theme)
    suspend fun setNotificationsEnabled(enabled: Boolean) = repo.setNotificationsEnabled(enabled)
}
