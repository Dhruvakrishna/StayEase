package com.example.stayease.ui.nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.core.connectivity.NetworkMonitor
import com.example.stayease.domain.repository.SettingsRepository
import com.example.stayease.domain.repository.UserSettings
import com.example.stayease.domain.usecase.LogoutUseCase
import com.example.stayease.domain.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    observe: ObserveSessionUseCase,
    private val logout: LogoutUseCase,
    private val settingsRepository: SettingsRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {
    
    val loggedIn = observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    
    val settings: StateFlow<UserSettings> = settingsRepository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val isOnline = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun logoutNow() { 
        viewModelScope.launch { logout() } 
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
        }
    }
}
