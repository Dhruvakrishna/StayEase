package com.example.stayease.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stay_remote_keys")
data class StayRemoteKeyEntity(
    @PrimaryKey val stayId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
