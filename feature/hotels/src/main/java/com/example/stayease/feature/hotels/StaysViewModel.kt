package com.example.stayease.feature.hotels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.stayease.domain.model.CmsContent
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.repository.CmsRepository
import com.example.stayease.domain.repository.LocationProvider
import com.example.stayease.domain.usecase.GetStaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaysViewModel @Inject constructor(
    private val getStays: GetStaysUseCase,
    private val locationProvider: LocationProvider,
    private val cmsRepository: CmsRepository
) : ViewModel() {

    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation: StateFlow<GeoPoint?> = _userLocation.asStateFlow()

    private val _radius = MutableStateFlow(5000)
    val radius: StateFlow<Int> = _radius.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val cmsContent: StateFlow<CmsContent> = cmsRepository.observeContent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CmsContent())

    init {
        viewModelScope.launch {
            cmsRepository.refreshContent()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val stays: Flow<PagingData<Stay>> = combine(
        _userLocation, 
        _radius, 
        _searchQuery,
        _selectedCategory
    ) { loc, rad, query, category ->
        DataParams(loc, rad, query, category)
    }
    .debounce(300) // Debounce rapid parameter changes to reduce API calls
    .distinctUntilChanged() // Only emit when params actually change
    .flatMapLatest { params ->
        val pivot = params.location ?: GeoPoint(41.8781, -87.6298) // Default to Chicago
        getStays(pivot = pivot, radiusMeters = params.radius, pageSize = 20)
            .map { pagingData ->
                pagingData.filter { stay ->
                    val matchesQuery = if (params.query.isBlank()) {
                        true
                    } else {
                        stay.name.contains(params.query, ignoreCase = true) ||
                        stay.category.contains(params.query, ignoreCase = true) ||
                        stay.address?.contains(params.query, ignoreCase = true) == true
                    }
                    val matchesCategory = params.category == null || stay.category == params.category
                    matchesQuery && matchesCategory
                }
            }
    }.cachedIn(viewModelScope)

    fun updateLocation(location: GeoPoint) {
        _userLocation.value = location
    }

    fun updateRadius(newRadius: Int) {
        if (_radius.value != newRadius) {
            _radius.value = newRadius
        }
    }

    fun onSearchQueryChanged(query: String) {
        if (_searchQuery.value != query) {
            _searchQuery.value = query
        }
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    private data class DataParams(
        val location: GeoPoint?,
        val radius: Int,
        val query: String,
        val category: String?
    )
}
