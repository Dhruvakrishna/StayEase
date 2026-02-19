package com.example.stayease.feature.auth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.core.result.AppResult
import com.example.stayease.core.telemetry.Telemetry
import com.example.stayease.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
  val email: String = "",
  val password: String = "",
  val loading: Boolean = false,
  val error: String? = null,
  val success: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val login: LoginUseCase,
  private val telemetry: Telemetry
) : ViewModel() {
  private val _state = MutableStateFlow(LoginUiState())
  val state = _state.asStateFlow()

  fun onEmail(v: String) { _state.value = _state.value.copy(email = v, error = null) }
  fun onPassword(v: String) { _state.value = _state.value.copy(password = v, error = null) }

  fun submit() {
    val s = _state.value
    if (s.loading) return
    viewModelScope.launch {
      _state.value = s.copy(loading = true, error = null)
      when (login(s.email.trim(), s.password)) {
        is AppResult.Ok -> {
          telemetry.setUserId(s.email.trim())
          telemetry.logEvent("login_success")
          _state.value = _state.value.copy(loading = false, success = true)
        }
        is AppResult.Err -> _state.value = _state.value.copy(loading = false, error = "Login failed. Check credentials.")
      }
    }
  }
  fun consumeSuccess() { _state.value = _state.value.copy(success = false) }
}
