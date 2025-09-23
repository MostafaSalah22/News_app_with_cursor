package com.example.firstcursorapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firstcursorapp.data.repository.NewsRepository
import com.example.firstcursorapp.data.model.NewsSource
import com.example.firstcursorapp.utils.NetworkMonitor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: NewsRepository,
    private val networkMonitor: NetworkMonitor,
    private val apiKey: String,
    private val defaultCountry: String = "EG"
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    private val intents = Channel<HomeIntent>(Channel.UNLIMITED)
    val intentFlow = intents.receiveAsFlow()

    fun process(intent: HomeIntent) {
        viewModelScope.launch { intents.send(intent) }
    }

    init {
        viewModelScope.launch {
            // Monitor network status
            networkMonitor.networkState().collect { isOnline ->
                _state.value = _state.value.copy(
                    isOnline = isOnline,
                    offlineMessage = if (!isOnline) "No internet connection." else null
                )
            }
        }
        
        viewModelScope.launch {
            intentFlow.collect { intent ->
                when (intent) {
                    is HomeIntent.Load -> load(defaultCountry)
                    is HomeIntent.Retry -> load(intent.country)
                    is HomeIntent.FilterByCategory -> filterByCategory(intent.category)
                }
            }
        }
    }

    private fun load(country: String) {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        
        // Check if we're online before making API call
        if (!networkMonitor.isNetworkAvailable()) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "No internet connection.",
                sources = emptyList()
            )
            return
        }
        
        viewModelScope.launch {
            val result = repository.getSources(country = country, apiKey = apiKey)
            result.onSuccess { list ->
                val categories = extractCategories(list)
                _state.value = _state.value.copy(
                    isLoading = false, 
                    sources = list, 
                    filteredSources = list,
                    availableCategories = categories,
                    errorMessage = null
                )
            }.onFailure { t ->
                _state.value = _state.value.copy(
                    isLoading = false, 
                    errorMessage = t.message ?: "Unknown error",
                    sources = emptyList(),
                    filteredSources = emptyList(),
                    availableCategories = emptyList()
                )
            }
        }
    }
    
    private fun filterByCategory(category: String?) {
        val currentSources = _state.value.sources
        val filtered = if (category == null) {
            currentSources
        } else {
            currentSources.filter { source ->
                source.category?.any { it.equals(category, ignoreCase = true) } == true
            }
        }
        
        _state.value = _state.value.copy(
            filteredSources = filtered,
            selectedCategory = category
        )
    }
    
    private fun extractCategories(sources: List<NewsSource>): List<String> {
        return sources
            .flatMap { source -> source.category ?: emptyList<String>() }
            .distinct()
            .sorted()
    }
}


