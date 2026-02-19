package com.example.stayease.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stayease.data.local.entity.StayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StayDao {
    @Query("SELECT * FROM stays ORDER BY rating DESC, nightlyPriceUsdEstimate ASC")
    fun observeStays(): Flow<List<StayEntity>>

    @Query("SELECT * FROM stays ORDER BY rating DESC, nightlyPriceUsdEstimate ASC")
    fun pagingSource(): PagingSource<Int, StayEntity>

    @Query("SELECT * FROM stays WHERE id = :id LIMIT 1")
    suspend fun getStay(id: Long): StayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<StayEntity>)

    @Query("DELETE FROM stays")
    suspend fun clear()
}
