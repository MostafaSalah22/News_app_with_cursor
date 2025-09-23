package com.example.firstcursorapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import com.example.firstcursorapp.feature.login.LoginIntent
import com.example.firstcursorapp.feature.login.LoginViewModel
import com.example.firstcursorapp.feature.auth.AuthViewModel
import com.example.firstcursorapp.feature.auth.AuthIntent
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val viewModel: LoginViewModel = koinViewModel()
    val authViewModel: AuthViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val authState by authViewModel.state.collectAsState()

    var emailVisible by rememberSaveable { mutableStateOf(false) }
    var passwordVisibleEnter by rememberSaveable { mutableStateOf(false) }
    var shrinkButton by rememberSaveable { mutableStateOf(false) }

    // Animations for fields
    LaunchedEffect(Unit) {
        emailVisible = true
        delay(100)
        passwordVisibleEnter = true
    }

    // Handle authentication success
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            shrinkButton = true
            delay(250)
            shrinkButton = false
            onLoginSuccess()
        }
    }

    // Show authentication errors
    LaunchedEffect(authState.errorMessage) {
        authState.errorMessage?.let { error ->
            // You can show a snackbar or toast here
            // For now, we'll just clear the error after a delay
            delay(3000)
            authViewModel.process(AuthIntent.ClearError)
        }
    }

    val widthFraction by animateFloatAsState(
        targetValue = if (shrinkButton) 0.4f else 1f,
        label = "loginWidth"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),

        // ✅ This prevents content overlap automatically
        contentWindowInsets = WindowInsets.safeDrawing,

        // **Top Bar** - Title and Subtitle
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // Add padding so it's not behind the status bar
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Log in to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },

        // **Bottom Bar** - Login Button + Footer
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // Add padding so it's not behind the navigation bar
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { 
                        // Use Firebase Authentication instead of local login
                        authViewModel.process(AuthIntent.SignIn(state.email, state.password))
                    },
                    modifier = Modifier
                        .fillMaxWidth(widthFraction)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !authState.isLoading
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = "Login")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Don't have an account? ")
                    Text(
                        text = "Create account",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            onSignUpClick()
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // **Main Content** - Login form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Important: Prevent overlap with bars
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Email Field
                    AnimatedVisibility(
                        visible = emailVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(durationMillis = 500)
                        )
                    ) {
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { viewModel.process(LoginIntent.UpdateEmail(it)) },
                            label = { Text("Email") },
                            placeholder = { Text("Enter your Email") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Email, contentDescription = null)
                            },
                            isError = state.emailError != null,
                            supportingText = {
                                state.emailError?.let { error -> Text(text = error) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    AnimatedVisibility(
                        visible = passwordVisibleEnter,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 500)
                        )
                    ) {
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { viewModel.process(LoginIntent.UpdatePassword(it)) },
                            label = { Text("Password") },
                            placeholder = { Text("Enter your Password") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Lock, contentDescription = null)
                            },
                            visualTransformation = if (state.isPasswordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.process(LoginIntent.TogglePasswordVisibility)
                                }) {
                                    val image = if (state.isPasswordVisible)
                                        Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                                    val description = if (state.isPasswordVisible)
                                        "Hide password" else "Show password"
                                    Icon(imageVector = image, contentDescription = description)
                                }
                            },
                            isError = state.passwordError != null,
                            supportingText = {
                                state.passwordError?.let { error -> Text(text = error) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Show authentication error if any
                    authState.errorMessage?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forget password?",
                            modifier = Modifier.clickable {
                                // TODO: navigate to forgot password
                            }
                        )
                    }
                }
            }
        }
    }
}

