package com.example.stayease.feature.hotels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.PointOfInterest
import com.example.stayease.domain.repository.LocationProvider
import com.example.stayease.domain.repository.PointOfInterestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AITravelViewModel @Inject constructor(
    private val poiRepository: PointOfInterestRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIUiState())
    val uiState: StateFlow<AIUiState> = _uiState.asStateFlow()

    private val _chatHistory = mutableStateListOf<Pair<String, Boolean>>()
    val chatHistory: List<Pair<String, Boolean>> = _chatHistory

    init {
        _chatHistory.add("Hello! I'm your StayEase AI Assistant. I can recommend nearby attractions based on your location. Ask me anything!" to false)
        fetchRecommendations()
    }

    fun sendMessage(query: String) {
        if (query.isBlank()) return
        
        _chatHistory.add(query to true)
        
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            // Simulating AI processing with real data context
            val location = locationProvider.getCurrentLocation()
            val response = if (location != null) {
                "I see you're near ${location.lat}, ${location.lon}. I'm searching for the best spots around you..."
            } else {
                "I'm unable to get your precise location, but I can still help! Tell me which city you're interested in."
            }
            _chatHistory.add(response to false)
            _uiState.update { it.copy(isAiLoading = false) }
        }
    }

    private fun fetchRecommendations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRecommendationsLoading = true) }
            val location = locationProvider.getCurrentLocation() ?: GeoPoint(48.8566, 2.3522) // Default to Paris if null
            
            poiRepository.getNearbyPointsOfInterest(location, "all", 5000)
                .catch { e -> _uiState.update { it.copy(error = e.message, isRecommendationsLoading = false) } }
                .collect { pois ->
                    _uiState.update { it.copy(recommendations = pois, isRecommendationsLoading = false) }
                }
        }
    }
}

data class AIUiState(
    val recommendations: List<PointOfInterest> = emptyList(),
    val isAiLoading: Boolean = false,
    val isRecommendationsLoading: Boolean = false,
    val error: String? = null
)
