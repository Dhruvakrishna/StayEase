package com.example.stayease.domain.usecase

import com.example.stayease.domain.repository.SettingsRepository
import com.example.stayease.domain.repository.UserSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<UserSettings> = repo.observeSettings()
}
