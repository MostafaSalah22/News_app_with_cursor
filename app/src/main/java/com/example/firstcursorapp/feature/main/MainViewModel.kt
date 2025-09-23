package com.example.firstcursorapp.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firstcursorapp.FavoriteNews
import com.example.firstcursorapp.Screen
import com.example.firstcursorapp.data.model.NewsSource
import com.example.firstcursorapp.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class MainState(
    val selectedScreen: Screen = Screen.News,
    val favoriteNews: List<FavoriteNews> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class MainIntent {
    data class SelectScreen(val screen: Screen) : MainIntent()
    data class ToggleFavorite(val news: NewsSource) : MainIntent()
    data class RemoveFavorite(val newsId: String) : MainIntent()
    object LoadFavorites : MainIntent()
    object ClearError : MainIntent()
}

class MainViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        // Load favorites from Firebase when ViewModel is created
        process(MainIntent.LoadFavorites)
    }

    fun process(intent: MainIntent) {
        viewModelScope.launch {
            when (intent) {
                is MainIntent.SelectScreen -> {
                    _state.value = _state.value.copy(selectedScreen = intent.screen)
                }
                
                is MainIntent.LoadFavorites -> {
                    _state.value = _state.value.copy(isLoading = true, errorMessage = null)
                    
                    favoritesRepository.getFavorites()
                        .catch { exception ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                errorMessage = "Failed to load favorites: ${exception.message}"
                            )
                        }
                        .collect { favorites ->
                            _state.value = _state.value.copy(
                                favoriteNews = favorites,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                }
                
                is MainIntent.ToggleFavorite -> {
                    val newFavorite = FavoriteNews(
                        id = intent.news.id ?: "",
                        title = intent.news.name ?: "",
                        description = intent.news.description ?: "",
                        imageUrl = intent.news.icon,
                        url = intent.news.url ?: intent.news.website ?: intent.news.link
                    )
                    
                    val result = favoritesRepository.toggleFavorite(newFavorite)
                    result.fold(
                        onSuccess = { isNowFavorite ->
                            // Success - the Flow will automatically update the UI
                        },
                        onFailure = { exception ->
                            _state.value = _state.value.copy(
                                errorMessage = "Failed to toggle favorite: ${exception.message}"
                            )
                        }
                    )
                }
                
                is MainIntent.RemoveFavorite -> {
                    val result = favoritesRepository.removeFavorite(intent.newsId)
                    result.fold(
                        onSuccess = {
                            // Success - the Flow will automatically update the UI
                        },
                        onFailure = { exception ->
                            _state.value = _state.value.copy(
                                errorMessage = "Failed to remove favorite: ${exception.message}"
                            )
                        }
                    )
                }
                
                MainIntent.ClearError -> {
                    _state.value = _state.value.copy(errorMessage = null)
                }
            }
        }
    }
}
