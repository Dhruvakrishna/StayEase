package com.example.stayease.domain.usecase

import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.repository.StayRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private val repo: StayRepository = mockk()
    private val useCase = ToggleFavoriteUseCase(repo)

    @Test
    fun `invoke should call repository toggleFavorite`() = runTest {
        // Given
        val id = 123L
        coEvery { repo.toggleFavorite(id) } returns AppResult.Ok(Unit)

        // When
        useCase(id)

        // Then
        coVerify { repo.toggleFavorite(id) }
    }
}
