package com.example.stayease.feature.asyncdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.core.domain.model.Trip
import com.example.stayease.core.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TripPlansViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    val trips: StateFlow<List<Trip>> = tripRepository.getTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTrip(title: String, destination: String, startDate: Long, endDate: Long, description: String) {
        viewModelScope.launch {
            val newTrip = Trip(
                id = UUID.randomUUID().toString(),
                title = title,
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                description = description
            )
            tripRepository.saveTrip(newTrip)
        }
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch {
            tripRepository.deleteTrip(id)
        }
    }
    
    fun syncTrips() {
        viewModelScope.launch {
            tripRepository.syncPendingChanges()
        }
    }
}
