package com.example.stayease.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stayease.data.local.entity.StayRemoteKeyEntity

@Dao
interface StayRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<StayRemoteKeyEntity>)

    @Query("SELECT * FROM stay_remote_keys WHERE stayId = :stayId")
    suspend fun remoteKeysStayId(stayId: Long): StayRemoteKeyEntity?

    @Query("DELETE FROM stay_remote_keys")
    suspend fun clearRemoteKeys()
}
