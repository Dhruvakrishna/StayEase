package com.example.stayease.data.repository

import com.example.stayease.core.result.AppResult
import com.example.stayease.core.result.safeCall
import com.example.stayease.data.local.dao.UserDao
import com.example.stayease.data.local.entity.UserEntity
import com.example.stayease.domain.model.User
import com.example.stayease.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dao: UserDao
) : UserRepository {

    override fun observeUser(): Flow<User?> =
        dao.observeUser().map { it?.toDomain() }

    override suspend fun updateProfile(name: String, avatarUrl: String?): AppResult<Unit> = safeCall {
        val current = dao.observeUser().map { it }.let { /* In a real app we'd get the current user ID */ }
        // For demo, we'll just insert a default user if none exists
        val user = UserEntity(
            id = "demo_user",
            name = name,
            email = "demo@stayease.com",
            avatarUrl = avatarUrl,
            totalBookings = 0,
            memberSince = System.currentTimeMillis()
        )
        dao.insert(user)
    }

    private fun UserEntity.toDomain() = User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        totalBookings = totalBookings,
        memberSince = memberSince
    )
}
