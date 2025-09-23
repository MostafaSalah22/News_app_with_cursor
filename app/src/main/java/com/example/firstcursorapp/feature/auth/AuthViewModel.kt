package com.example.firstcursorapp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firstcursorapp.data.firebase.AuthService
import com.example.firstcursorapp.data.local.NewsDatabase
import com.example.firstcursorapp.data.local.dao.UserProfileDao
import com.example.firstcursorapp.data.local.entities.UserProfile
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val errorMessage: String? = null,
    val userProfile: UserProfile? = null
)

sealed class AuthIntent {
    data class SignIn(val email: String, val password: String) : AuthIntent()
    data class SignUp(val email: String, val password: String, val displayName: String?) : AuthIntent()
    data class UpdateProfile(val displayName: String?, val photoUrl: String?) : AuthIntent()
    object SignOut : AuthIntent()
    object DeleteAccount : AuthIntent()
    object ClearError : AuthIntent()
}

class AuthViewModel(
    private val authService: AuthService,
    private val database: NewsDatabase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val userProfileDao = database.userProfileDao()

    init {
        // Observe authentication state
        viewModelScope.launch {
            val currentUser = authService.currentUser
            if (currentUser != null) {
                loadUserProfile(currentUser.uid)
            } else {
                _state.value = _state.value.copy(
                    isLoggedIn = false,
                    currentUser = null,
                    userProfile = null
                )
            }
        }
    }

    fun process(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignIn -> signIn(intent.email, intent.password)
            is AuthIntent.SignUp -> signUp(intent.email, intent.password, intent.displayName)
            is AuthIntent.UpdateProfile -> updateProfile(intent.displayName, intent.photoUrl)
            AuthIntent.SignOut -> signOut()
            AuthIntent.DeleteAccount -> deleteAccount()
            AuthIntent.ClearError -> clearError()
        }
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            authService.signInWithEmailAndPassword(email, password)
                .onSuccess { user ->
                    loadUserProfile(user.uid)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Sign in failed"
                    )
                }
        }
    }

    private fun signUp(email: String, password: String, displayName: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            authService.createUserWithEmailAndPassword(email, password, displayName)
                .onSuccess { user ->
                    // Create user profile
                    val userProfile = UserProfile(
                        userId = user.uid,
                        email = email,
                        displayName = displayName,
                        photoUrl = user.photoUrl?.toString()
                    )
                    
                    userProfileDao.insertUserProfile(userProfile)
                    
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        userProfile = userProfile
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Account creation failed"
                    )
                }
        }
    }

    private fun updateProfile(displayName: String?, photoUrl: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            authService.updateUserProfile(displayName, photoUrl)
                .onSuccess {
                    val currentUser = _state.value.currentUser
                    if (currentUser != null) {
                        loadUserProfile(currentUser.uid)
                    }
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Profile update failed"
                    )
                }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            authService.signOut()
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        currentUser = null,
                        userProfile = null
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Sign out failed"
                    )
                }
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            val currentUser = _state.value.currentUser
            if (currentUser != null) {
                // Delete user profile from local database
                userProfileDao.deleteUserProfileById(currentUser.uid)
                
                authService.deleteUser()
                    .onSuccess {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isLoggedIn = false,
                            currentUser = null,
                            userProfile = null
                        )
                    }
                    .onFailure { error ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Account deletion failed"
                        )
                    }
            }
        }
    }

    private fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    private suspend fun loadUserProfile(userId: String) {
        try {
            val userProfile = userProfileDao.getUserProfileSync(userId)
            val currentUser = authService.currentUser
            
            _state.value = _state.value.copy(
                isLoading = false,
                isLoggedIn = true,
                currentUser = currentUser,
                userProfile = userProfile
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "Failed to load user profile"
            )
        }
    }
}
