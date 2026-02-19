package com.example.stayease.domain.usecase

import androidx.paging.PagingData
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.repository.StayRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetStaysUseCaseTest {

    private val repository = mockk<StayRepository>()
    private val getStaysUseCase = GetStaysUseCase(repository)

    @Test
    fun `invoke should call repository staysNear with correct parameters`() = runTest {
        // Arrange
        val pivot = GeoPoint(40.7128, -74.0060)
        val radius = 2500
        val pageSize = 20
        val expectedPagingData = flowOf(PagingData.empty<Stay>())
        
        every { repository.staysNear(pivot, radius, pageSize) } returns expectedPagingData

        // Act
        val result = getStaysUseCase(pivot, radius, pageSize)

        // Assert
        assertThat(result).isEqualTo(expectedPagingData)
    }
}
