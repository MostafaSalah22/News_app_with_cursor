package com.example.firstcursorapp.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firstcursorapp.FavoriteNews
import com.example.firstcursorapp.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class FavoritesState(
    val favoriteNews: List<FavoriteNews> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class FavoritesIntent {
    data class RemoveFavorite(val newsId: String) : FavoritesIntent()
    data class AddFavorite(val news: FavoriteNews) : FavoritesIntent()
    data class ToggleFavorite(val news: FavoriteNews) : FavoritesIntent()
    data class SetFavorites(val favorites: List<FavoriteNews>) : FavoritesIntent()
    object LoadFavorites : FavoritesIntent()
    object ClearError : FavoritesIntent()
}

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        // Load favorites from Firebase when ViewModel is created
        process(FavoritesIntent.LoadFavorites)
    }

    fun process(intent: FavoritesIntent) {
        viewModelScope.launch {
            when (intent) {
                is FavoritesIntent.LoadFavorites -> {
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
                
                is FavoritesIntent.RemoveFavorite -> {
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
                
                is FavoritesIntent.AddFavorite -> {
                    val result = favoritesRepository.addFavorite(intent.news)
                    result.fold(
                        onSuccess = {
                            // Success - the Flow will automatically update the UI
                        },
                        onFailure = { exception ->
                            _state.value = _state.value.copy(
                                errorMessage = "Failed to add favorite: ${exception.message}"
                            )
                        }
                    )
                }
                
                is FavoritesIntent.ToggleFavorite -> {
                    val result = favoritesRepository.toggleFavorite(intent.news)
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
                
                is FavoritesIntent.SetFavorites -> {
                    // This is for backward compatibility with existing code
                    // In the new implementation, favorites are managed through Firebase
                    _state.value = _state.value.copy(favoriteNews = intent.favorites)
                }
                
                FavoritesIntent.ClearError -> {
                    _state.value = _state.value.copy(errorMessage = null)
                }
            }
        }
    }
}
