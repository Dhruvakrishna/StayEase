package com.example.stayease.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stayease.core.domain.model.Trip

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val title: String,
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val description: String,
    val isPendingSync: Boolean = false
)

fun TripEntity.toDomain() = Trip(
    id = id,
    title = title,
    destination = destination,
    startDate = startDate,
    endDate = endDate,
    description = description
)

fun Trip.toEntity(isPendingSync: Boolean = false) = TripEntity(
    id = id,
    title = title,
    destination = destination,
    startDate = startDate,
    endDate = endDate,
    description = description,
    isPendingSync = isPendingSync
)
