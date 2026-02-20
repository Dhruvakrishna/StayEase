package com.example.stayease.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.domain.repository.AppTheme
import com.example.stayease.domain.repository.UserSettings
import com.example.stayease.domain.usecase.ObserveSettingsUseCase
import com.example.stayease.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase,
    private val updateSettings: UpdateSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<UserSettings> = observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            updateSettings.setTheme(theme)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettings.setNotificationsEnabled(enabled)
        }
    }
}
