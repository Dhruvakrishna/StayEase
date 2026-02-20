package com.example.stayease.domain.repository

import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(): Flow<User?>
    suspend fun updateProfile(name: String, avatarUrl: String?): AppResult<Unit>
}
