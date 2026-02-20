package com.example.stayease.domain.usecase

import com.example.stayease.domain.model.User
import com.example.stayease.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserUseCase @Inject constructor(private val repo: UserRepository) {
    operator fun invoke(): Flow<User?> = repo.observeUser()
}
