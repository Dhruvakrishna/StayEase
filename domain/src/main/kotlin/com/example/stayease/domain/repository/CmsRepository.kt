package com.example.stayease.domain.repository

import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.CmsContent
import kotlinx.coroutines.flow.Flow

interface CmsRepository {
    fun observeContent(): Flow<CmsContent>
    suspend fun refreshContent(): AppResult<Unit>
}
