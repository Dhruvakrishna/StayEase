package com.example.stayease.ui.nav
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.domain.usecase.LogoutUseCase
import com.example.stayease.domain.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
  observe: ObserveSessionUseCase,
  private val logout: LogoutUseCase
) : ViewModel() {
  val loggedIn = observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
  fun logoutNow() { viewModelScope.launch { logout() } }
}
