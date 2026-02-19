package com.example.stayease.feature.bookings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.domain.usecase.GetBookingsUseCase
import com.example.stayease.domain.usecase.SyncBookingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingsViewModel @Inject constructor(
  getBookings: GetBookingsUseCase,
  private val sync: SyncBookingsUseCase
) : ViewModel() {
  val bookings = getBookings()
  fun syncNow() { viewModelScope.launch { sync() } }
}
