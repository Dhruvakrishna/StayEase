package com.example.stayease.domain.usecase

import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(private val repo: UserRepository) {
    suspend operator fun invoke(name: String, avatarUrl: String?): AppResult<Unit> =
        repo.updateProfile(name, avatarUrl)
}
