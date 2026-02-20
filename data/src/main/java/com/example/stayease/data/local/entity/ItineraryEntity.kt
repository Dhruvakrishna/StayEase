package com.example.stayease.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.stayease.core.domain.model.ItineraryItem
import com.example.stayease.core.domain.model.ItineraryType

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ItineraryEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val title: String,
    val startTime: Long,
    val location: String,
    val notes: String,
    val type: String,
    val isPendingSync: Boolean = false
)

fun ItineraryEntity.toDomain() = ItineraryItem(
    id = id,
    tripId = tripId,
    title = title,
    startTime = startTime,
    location = location,
    notes = notes,
    type = ItineraryType.valueOf(type)
)

fun ItineraryItem.toEntity(isPendingSync: Boolean = false) = ItineraryEntity(
    id = id,
    tripId = tripId,
    title = title,
    startTime = startTime,
    location = location,
    notes = notes,
    type = type.name,
    isPendingSync = isPendingSync
)
