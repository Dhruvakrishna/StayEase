package com.example.stayease.feature.hotels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.repository.LocationProvider
import com.example.stayease.domain.usecase.GetStaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StaysViewModel @Inject constructor(
    private val getStays: GetStaysUseCase,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation: StateFlow<GeoPoint?> = _userLocation.asStateFlow()

    private val _radius = MutableStateFlow(2500)
    val radius: StateFlow<Int> = _radius.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val stays: Flow<PagingData<Stay>> = combine(_userLocation, _radius) { loc, rad ->
        loc to rad
    }.flatMapLatest { (loc, rad) ->
        val pivot = loc ?: GeoPoint(41.8781, -87.6298) // Default to Chicago if no location
        getStays(pivot = pivot, radiusMeters = rad, pageSize = 20)
    }.cachedIn(viewModelScope)

    fun updateLocation(location: GeoPoint) {
        _userLocation.value = location
    }

    fun updateRadius(newRadius: Int) {
        _radius.value = newRadius
    }
}
