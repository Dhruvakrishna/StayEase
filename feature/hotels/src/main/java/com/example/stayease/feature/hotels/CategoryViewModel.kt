package com.example.stayease.feature.hotels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.PointOfInterest
import com.example.stayease.domain.model.TravelEvent
import com.example.stayease.domain.repository.EventsRepository
import com.example.stayease.domain.repository.LocationProvider
import com.example.stayease.domain.repository.PointOfInterestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val poiRepository: PointOfInterestRepository,
    private val eventsRepository: EventsRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentCategory: String? = null
    private var allItems: List<Any> = emptyList() // Can be PointOfInterest or TravelEvent

    fun fetchCategoryData(category: String) {
        if (currentCategory == category && allItems.isNotEmpty()) return
        currentCategory = category
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val location = locationProvider.getCurrentLocation() ?: GeoPoint(41.8781, -87.6298)
            
            // Optimization: Use server-side filtering by passing the category to the repository
            val poiFlow = poiRepository.getNearbyPointsOfInterest(location, category, 15000)
            val eventsFlow = eventsRepository.getEvents(location, category, 20000)

            combine(poiFlow, eventsFlow) { pois, events ->
                // Merge and prioritize Events (real-time data) over POIs (static data)
                // Filtering is now handled on the server (Overpass), so we just merge and sort.
                (events + pois).sortedByDescending {
                    when(it) {
                        is TravelEvent -> 2 // Events have higher priority
                        is PointOfInterest -> 1
                        else -> 0
                    }
                }
            }.catch { e -> 
                _uiState.update { it.copy(error = e.message, isLoading = false) } 
            }.collect { merged ->
                allItems = merged
                applyFilter()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    private fun applyFilter() {
        val query = _searchQuery.value.lowercase()
        val filtered = if (query.isBlank()) {
            allItems
        } else {
            allItems.filter { item ->
                when(item) {
                    is TravelEvent -> item.name.lowercase().contains(query) || item.category.lowercase().contains(query)
                    is PointOfInterest -> item.name.lowercase().contains(query) || item.category.lowercase().contains(query)
                    else -> false
                }
            }
        }
        _uiState.update { it.copy(items = filtered, isLoading = false) }
    }

    fun refresh() {
        currentCategory?.let { 
            allItems = emptyList()
            fetchCategoryData(it) 
        }
    }
}

data class CategoryUiState(
    val items: List<Any> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
