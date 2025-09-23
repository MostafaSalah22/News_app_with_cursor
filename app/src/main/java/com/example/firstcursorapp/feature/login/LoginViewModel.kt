package com.example.firstcursorapp.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)

sealed class LoginIntent {
    data class UpdateEmail(val email: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    object TogglePasswordVisibility : LoginIntent()
    object Login : LoginIntent()
    object ClearErrors : LoginIntent()
}

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun process(intent: LoginIntent) {
        viewModelScope.launch {
            when (intent) {
                is LoginIntent.UpdateEmail -> {
                    _state.value = _state.value.copy(
                        email = intent.email,
                        emailError = null
                    )
                }
                is LoginIntent.UpdatePassword -> {
                    _state.value = _state.value.copy(
                        password = intent.password,
                        passwordError = null
                    )
                }
                is LoginIntent.TogglePasswordVisibility -> {
                    val currentState = _state.value
                    _state.value = currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
                }
                is LoginIntent.Login -> {
                    validateAndLogin()
                }
                is LoginIntent.ClearErrors -> {
                    _state.value = _state.value.copy(
                        emailError = null,
                        passwordError = null
                    )
                }
            }
        }
    }

    private fun validateAndLogin() {
        val currentState = _state.value
        val emailError = if (currentState.email.isBlank()) "Email is required" else null
        val passwordError = if (currentState.password.isBlank()) "Password is required" else null

        _state.value = currentState.copy(
            emailError = emailError,
            passwordError = passwordError,
            isLoading = emailError == null && passwordError == null
        )

        // If validation passes, the loading state will be handled by the screen
        // The actual navigation logic should be handled by the parent composable
    }

    fun isValidLogin(): Boolean {
        val currentState = _state.value
        return currentState.email.isNotBlank() && currentState.password.isNotBlank() && 
               currentState.emailError == null && currentState.passwordError == null
    }
}
