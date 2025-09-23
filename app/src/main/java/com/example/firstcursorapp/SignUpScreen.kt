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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.firstcursorapp.feature.auth.AuthViewModel
import com.example.firstcursorapp.feature.auth.AuthIntent
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.state.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var shrinkButton by rememberSaveable { mutableStateOf(false) }

    // Animations for fields
    var nameVisible by rememberSaveable { mutableStateOf(false) }
    var emailVisible by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        nameVisible = true
        delay(100)
        emailVisible = true
        delay(100)
        passwordVisible = true
        delay(100)
        confirmPasswordVisible = true
    }

    // Handle authentication success
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            shrinkButton = true
            delay(250)
            shrinkButton = false
            onSignUpSuccess()
        }
    }

    // Show authentication errors
    LaunchedEffect(authState.errorMessage) {
        authState.errorMessage?.let { error ->
            delay(3000)
            authViewModel.process(AuthIntent.ClearError)
        }
    }

    val widthFraction by animateFloatAsState(
        targetValue = if (shrinkButton) 0.4f else 1f,
        label = "signupWidth"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Join us today",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Create your account to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Name Field
                    AnimatedVisibility(
                        visible = nameVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(durationMillis = 500)
                        )
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("Enter your full name") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    AnimatedVisibility(
                        visible = emailVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(durationMillis = 500)
                        )
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            placeholder = { Text("Enter your email") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Email, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    AnimatedVisibility(
                        visible = passwordVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 500)
                        )
                    ) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            placeholder = { Text("Enter your password") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Lock, contentDescription = null)
                            },
                            visualTransformation = if (isPasswordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    isPasswordVisible = !isPasswordVisible
                                }) {
                                    val image = if (isPasswordVisible)
                                        Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                                    val description = if (isPasswordVisible)
                                        "Hide password" else "Show password"
                                    Icon(imageVector = image, contentDescription = description)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Field
                    AnimatedVisibility(
                        visible = confirmPasswordVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 500)
                        )
                    ) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            placeholder = { Text("Confirm your password") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Lock, contentDescription = null)
                            },
                            visualTransformation = if (isConfirmPasswordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    isConfirmPasswordVisible = !isConfirmPasswordVisible
                                }) {
                                    val image = if (isConfirmPasswordVisible)
                                        Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                                    val description = if (isConfirmPasswordVisible)
                                        "Hide password" else "Show password"
                                    Icon(imageVector = image, contentDescription = description)
                                }
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

                    // Validation messages
                    if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                        Text(
                            text = "Passwords do not match",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (password.isNotEmpty() && password.length < 6) {
                        Text(
                            text = "Password must be at least 6 characters",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button
            Button(
                onClick = { 
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && 
                        password == confirmPassword && password.length >= 6) {
                        authViewModel.process(AuthIntent.SignUp(email, password, name))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(widthFraction)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(12.dp),
                enabled = !authState.isLoading && 
                         name.isNotBlank() && 
                         email.isNotBlank() && 
                         password.isNotBlank() && 
                         password == confirmPassword && 
                         password.length >= 6
            ) {
                if (authState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = "Create Account")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Login Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Already have an account? ")
                Text(
                    text = "Sign in",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onBackClick() }
                )
            }
        }
    }
}
