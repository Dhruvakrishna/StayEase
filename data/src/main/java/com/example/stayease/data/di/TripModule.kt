package com.example.stayease.data.di

import android.content.Context
import androidx.room.Room
import com.example.stayease.data.local.AppDatabase
import com.example.stayease.data.local.dao.TripDao
import com.example.stayease.core.domain.repository.TripRepository
import com.example.stayease.data.repository.TripRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TripRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository
}

@Module
@InstallIn(SingletonComponent::class)
object TripDaoModule {

    @Provides
    @Singleton
    fun provideTripDao(appDatabase: AppDatabase): TripDao {
        return appDatabase.tripDao()
    }
}
