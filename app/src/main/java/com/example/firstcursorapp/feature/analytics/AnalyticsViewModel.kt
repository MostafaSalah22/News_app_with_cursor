package com.example.firstcursorapp.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firstcursorapp.data.repository.AnalyticsRepository
import com.example.firstcursorapp.feature.auth.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AnalyticsState(
    val events: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class AnalyticsIntent {
    data class Log(val action: String) : AnalyticsIntent()
    object Clear : AnalyticsIntent()
    object LoadAnalytics : AnalyticsIntent()
}

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {
    
    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()
    
    init {
        // Load analytics when user changes
        viewModelScope.launch {
            authViewModel.state.collect { authState ->
                authState.currentUser?.uid?.let { userId ->
                    loadAnalytics(userId)
                } ?: run {
                    // Clear analytics when user logs out
                    _state.value = _state.value.copy(events = emptyMap())
                }
            }
        }
    }
    
    fun process(intent: AnalyticsIntent) {
        when (intent) {
            is AnalyticsIntent.Log -> logAction(intent.action)
            AnalyticsIntent.Clear -> clearAnalytics()
            AnalyticsIntent.LoadAnalytics -> {
                authViewModel.state.value.currentUser?.uid?.let { userId ->
                    loadAnalytics(userId)
                }
            }
        }
    }
    
    private fun logAction(action: String) {
        viewModelScope.launch {
            try {
                val userId = authViewModel.state.value.currentUser?.uid
                if (userId != null) {
                    analyticsRepository.logAction(userId, action)
                    // Reload analytics to update the UI
                    loadAnalytics(userId)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to log action: ${e.message}"
                )
            }
        }
    }
    
    private fun clearAnalytics() {
        viewModelScope.launch {
            try {
                val userId = authViewModel.state.value.currentUser?.uid
                if (userId != null) {
                    analyticsRepository.clearAnalyticsForUser(userId)
                    _state.value = _state.value.copy(
                        events = emptyMap()
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to clear analytics: ${e.message}"
                )
            }
        }
    }
    
    private fun loadAnalytics(userId: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                
                val analyticsSummary = analyticsRepository.getAnalyticsSummaryByUser(userId)
                _state.value = _state.value.copy(
                    events = analyticsSummary,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load analytics: ${e.message}"
                )
            }
        }
    }
}
