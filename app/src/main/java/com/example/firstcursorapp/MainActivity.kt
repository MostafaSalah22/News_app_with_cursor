package com.example.firstcursorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.firstcursorapp.ui.theme.FirstCursorAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.example.firstcursorapp.feature.settings.SettingsViewModel
import com.example.firstcursorapp.feature.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val settingsVm: SettingsViewModel = koinViewModel()
            val authVm: AuthViewModel = koinViewModel()
            val settingsState by settingsVm.state.collectAsState()
            val authState by authVm.state.collectAsState()
            FirstCursorAppTheme(darkTheme = settingsState.isDarkTheme) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("splash") {
                        SplashScreen(
                            onSplashComplete = { 
                                // Check if user is logged in
                                if (authState.isLoggedIn) {
                                    // User is logged in, go directly to home
                                    navController.navigate("home") { 
                                        popUpTo("splash") { inclusive = true } 
                                    }
                                } else {
                                    // User is not logged in, go to login screen
                                    navController.navigate("login") { 
                                        popUpTo("splash") { inclusive = true } 
                                    }
                                }
                            }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            modifier = Modifier,
                            onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                            onSignUpClick = { navController.navigate("signup") }
                        )
                    }
                    composable("signup") {
                        SignUpScreen(
                            modifier = Modifier,
                            onSignUpSuccess = { navController.navigate("home") { popUpTo("signup") { inclusive = true } } },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("home") { 
                        MainScreen(
                            navController = navController,
                            onLogout = {
                                navController.navigate("login") { 
                                    popUpTo("home") { inclusive = true } 
                                }
                            }
                        )
                    }
                    composable(
                        "webview/{url}",
                        arguments = listOf(
                            navArgument("url") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val url = backStackEntry.arguments?.getString("url") ?: ""
                        WebViewScreen(
                            url = url,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    // New screens
                    composable("settings") {
                        SettingsScreen(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    
                    // Search screen removed per request
                }
            }
        }
    }
}