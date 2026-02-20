package com.example.stayease.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stayease.data.local.converter.Converters
import com.example.stayease.data.local.dao.BookingDao
import com.example.stayease.data.local.dao.StayDao
import com.example.stayease.data.local.dao.StayRemoteKeyDao
import com.example.stayease.data.local.dao.UserDao
import com.example.stayease.data.local.dao.TripDao
import com.example.stayease.data.local.entity.BookingEntity
import com.example.stayease.data.local.entity.StayEntity
import com.example.stayease.data.local.entity.StayRemoteKeyEntity
import com.example.stayease.data.local.entity.UserEntity
import com.example.stayease.data.local.entity.TripEntity
import com.example.stayease.data.local.entity.ItineraryEntity

@Database(
    entities = [
        StayEntity::class, 
        BookingEntity::class, 
        StayRemoteKeyEntity::class,
        UserEntity::class,
        TripEntity::class,
        ItineraryEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stayDao(): StayDao
    abstract fun bookingDao(): BookingDao
    abstract fun stayRemoteKeyDao(): StayRemoteKeyDao
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
}
