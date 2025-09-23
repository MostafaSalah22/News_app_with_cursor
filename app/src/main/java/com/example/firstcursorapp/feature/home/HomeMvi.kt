package com.example.firstcursorapp.feature.home

import com.example.firstcursorapp.data.model.NewsSource

sealed interface HomeIntent {
    data object Load : HomeIntent
    data class Retry(val country: String = "US") : HomeIntent
    data class FilterByCategory(val category: String?) : HomeIntent
}

data class HomeState(
    val isLoading: Boolean = false,
    val sources: List<NewsSource> = emptyList(),
    val filteredSources: List<NewsSource> = emptyList(),
    val availableCategories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val errorMessage: String? = null,
    val isOnline: Boolean = true,
    val offlineMessage: String? = null
)


