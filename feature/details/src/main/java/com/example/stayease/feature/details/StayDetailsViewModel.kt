package com.example.stayease.feature.details
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.core.result.AppResult
import com.example.stayease.core.telemetry.Telemetry
import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.usecase.CreateBookingUseCase
import com.example.stayease.domain.usecase.GetStayDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

data class DetailsUiState(
  val loading: Boolean = true,
  val stay: Stay? = null,
  val error: String? = null,
  val checkInEpochDay: Long = 20000,
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
  private val telemetry: Telemetry
) : ViewModel() {

  private val id: Long = checkNotNull(savedStateHandle["id"])
  private val _state = MutableStateFlow(DetailsUiState())
  val state = _state.asStateFlow()

  init {
    viewModelScope.launch {
      when (val res = getDetails(id)) {
        is AppResult.Ok -> _state.value = _state.value.copy(loading = false, stay = res.value)
        is AppResult.Err -> _state.value = _state.value.copy(loading = false, error = "Could not load details.")
      }
    }
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
          _state.value = _state.value.copy(message = "Booking created locally (ID ${res.value}). It will sync in background to your Booking API.")
        }
        is AppResult.Err -> _state.value = _state.value.copy(message = "Could not create booking.")
      }
    }
  }
  fun clearMessage() { _state.value = _state.value.copy(message = null) }
}
