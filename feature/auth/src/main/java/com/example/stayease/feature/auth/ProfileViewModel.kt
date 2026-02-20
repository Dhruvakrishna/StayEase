package com.example.stayease.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.domain.model.User
import com.example.stayease.domain.usecase.ObserveUserUseCase
import com.example.stayease.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeUser: ObserveUserUseCase,
    private val updateProfile: UpdateProfileUseCase
) : ViewModel() {

    val user: StateFlow<User?> = observeUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateName(newName: String) {
        viewModelScope.launch {
            updateProfile(newName, user.value?.avatarUrl)
        }
    }
}
