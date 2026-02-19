package com.example.stayease.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stayease.data.local.dao.BookingDao
import com.example.stayease.data.local.dao.StayDao
import com.example.stayease.data.local.dao.StayRemoteKeyDao
import com.example.stayease.data.local.entity.BookingEntity
import com.example.stayease.data.local.entity.StayEntity
import com.example.stayease.data.local.entity.StayRemoteKeyEntity

@Database(
    entities = [StayEntity::class, BookingEntity::class, StayRemoteKeyEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stayDao(): StayDao
    abstract fun bookingDao(): BookingDao
    abstract fun stayRemoteKeyDao(): StayRemoteKeyDao
}
