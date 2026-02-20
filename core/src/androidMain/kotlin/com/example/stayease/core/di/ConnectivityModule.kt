package com.example.stayease.core.di

import com.example.stayease.core.connectivity.ConnectivityManagerNetworkMonitor
import com.example.stayease.core.connectivity.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ConnectivityModule {
    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor
}
