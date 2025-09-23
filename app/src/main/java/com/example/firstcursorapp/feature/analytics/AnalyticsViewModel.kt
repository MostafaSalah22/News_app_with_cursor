package com.example.firstcursorapp.feature.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()
    
    // In-memory storage for analytics (in a real app, you'd use a database)
    private val analyticsEvents = mutableMapOf<String, Int>()
    
    init {
        loadAnalytics()
    }
    
    fun process(intent: AnalyticsIntent) {
        when (intent) {
            is AnalyticsIntent.Log -> logAction(intent.action)
            AnalyticsIntent.Clear -> clearAnalytics()
            AnalyticsIntent.LoadAnalytics -> loadAnalytics()
        }
    }
    
    private fun logAction(action: String) {
        viewModelScope.launch {
            try {
                val currentCount = analyticsEvents[action] ?: 0
                analyticsEvents[action] = currentCount + 1
                
                _state.value = _state.value.copy(
                    events = analyticsEvents.toMap()
                )
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
                analyticsEvents.clear()
                _state.value = _state.value.copy(
                    events = emptyMap()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to clear analytics: ${e.message}"
                )
            }
        }
    }
    
    private fun loadAnalytics() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                
                // In a real app, you'd load from a database
                // For now, we'll just use the in-memory data
                _state.value = _state.value.copy(
                    events = analyticsEvents.toMap(),
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
