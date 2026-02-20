package com.example.stayease.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
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

    @Query("SELECT COUNT(*) FROM stays")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<StayEntity>)

    @Query("UPDATE stays SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("SELECT * FROM stays WHERE isFavorite = 1")
    fun observeFavorites(): Flow<List<StayEntity>>

    @Query("DELETE FROM stays WHERE isFavorite = 0")
    suspend fun clearNonFavorites()

    @Query("DELETE FROM stays")
    suspend fun clear()
}
