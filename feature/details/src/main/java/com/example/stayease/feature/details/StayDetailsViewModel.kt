package com.example.stayease.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.core.domain.model.Trip
import com.example.stayease.core.domain.repository.TripRepository
import com.example.stayease.core.result.AppResult
import com.example.stayease.core.telemetry.Telemetry
import com.example.stayease.domain.model.PointOfInterest
import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.usecase.AddToItineraryUseCase
import com.example.stayease.domain.usecase.CreateBookingUseCase
import com.example.stayease.domain.usecase.GetNearbyPOIsUseCase
import com.example.stayease.domain.usecase.GetStayDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

data class DetailsUiState(
    val loading: Boolean = true,
    val stay: Stay? = null,
    val pois: List<PointOfInterest> = emptyList(),
    val trips: List<Trip> = emptyList(),
    val weatherTemp: Double? = null,
    val error: String? = null,
    val checkInEpochDay: Long = System.currentTimeMillis() / (24 * 60 * 60 * 1000),
    val nights: Int = 2,
    val guests: Int = 2,
    val rooms: Int = 1,
    val roomType: String = "Standard",
    val specialRequests: String = "",
    val message: String? = null
)

@HiltViewModel
class StayDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDetails: GetStayDetailsUseCase,
    private val createBooking: CreateBookingUseCase,
    private val getNearbyPOIs: GetNearbyPOIsUseCase,
    private val addToItinerary: AddToItineraryUseCase,
    private val tripRepository: TripRepository,
    private val telemetry: Telemetry
) : ViewModel() {

    private val id: Long = checkNotNull(savedStateHandle["id"])
    private val _state = MutableStateFlow(DetailsUiState())
    val state = _state.asStateFlow()

    init {
        loadDetails()
        loadTrips()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            when (val res = getDetails(id)) {
                is AppResult.Ok -> {
                    val stay = res.value
                    _state.value = _state.value.copy(loading = false, stay = stay)
                    loadNearbyPOIs(stay)
                    fetchWeather(stay)
                }
                is AppResult.Err -> _state.value = _state.value.copy(loading = false, error = "Could not load details.")
            }
        }
    }

    private fun loadNearbyPOIs(stay: Stay) {
        viewModelScope.launch {
            getNearbyPOIs(stay.location, radiusMeters = 2000).collectLatest { poiList ->
                _state.value = _state.value.copy(pois = poiList)
            }
        }
    }

    private fun loadTrips() {
        viewModelScope.launch {
            tripRepository.getTrips().collectLatest { trips ->
                _state.value = _state.value.copy(trips = trips)
            }
        }
    }

    private fun fetchWeather(stay: Stay) {
        // Using Open-Meteo free API
        viewModelScope.launch {
            try {
                // Simplified mock/real call for demonstration
                // In a real app, this would be in a repository
                _state.value = _state.value.copy(weatherTemp = 24.5) // Mocked for now
            } catch (e: Exception) {
                // Ignore weather errors
            }
        }
    }

    fun addItemToTrip(poi: PointOfInterest, tripId: String) {
        viewModelScope.launch {
            addToItinerary(tripId, poi)
            _state.value = _state.value.copy(message = "${poi.name} added to your itinerary!")
        }
    }

    fun setCheckInDate(epochDay: Long) {
        _state.value = _state.value.copy(checkInEpochDay = epochDay)
    }

    fun incNights() { _state.value = _state.value.copy(nights = (_state.value.nights + 1).coerceAtMost(14)) }
    fun decNights() { _state.value = _state.value.copy(nights = (_state.value.nights - 1).coerceAtLeast(1)) }
    fun incGuests() { _state.value = _state.value.copy(guests = (_state.value.guests + 1).coerceAtMost(6)) }
    fun decGuests() { _state.value = _state.value.copy(guests = (_state.value.guests - 1).coerceAtLeast(1)) }
    fun setRoomType(v: String) { _state.value = _state.value.copy(roomType = v) }
    fun setRequests(v: String) { _state.value = _state.value.copy(specialRequests = v) }

    fun book() {
        val stay = _state.value.stay ?: return
        val s = _state.value
        val checkOut = s.checkInEpochDay + max(1, s.nights).toLong()
        viewModelScope.launch {
            when (val res = createBooking(stay, s.checkInEpochDay, checkOut, s.guests, s.rooms, s.roomType, s.specialRequests.ifBlank { null })) {
                is AppResult.Ok -> {
                    telemetry.logEvent("booking_create_tapped", mapOf("stayId" to stay.id))
                    _state.value = _state.value.copy(message = "Booking created locally (ID ${res.value}). It will sync in background.")
                }
                is AppResult.Err -> _state.value = _state.value.copy(message = "Could not create booking.")
            }
        }
    }
    fun clearMessage() { _state.value = _state.value.copy(message = null) }
}
