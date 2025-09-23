package com.example.firstcursorapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firstcursorapp.feature.main.MainIntent
import com.example.firstcursorapp.feature.main.MainViewModel
import com.example.firstcursorapp.feature.analytics.AnalyticsViewModel
import com.example.firstcursorapp.feature.analytics.AnalyticsIntent
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object News : Screen("news", "News", Icons.Filled.Newspaper)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.Favorite)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    onLogout: () -> Unit = {}
) {
    val viewModel: MainViewModel = koinViewModel()
    val analyticsVm: AnalyticsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Screen.News.icon,
                            contentDescription = Screen.News.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(Screen.News.title) },
                    selected = state.selectedScreen == Screen.News,
                    onClick = { 
                        viewModel.process(MainIntent.SelectScreen(Screen.News))
                        analyticsVm.process(AnalyticsIntent.Log("tab_news"))
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Screen.Favorites.icon,
                            contentDescription = Screen.Favorites.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(Screen.Favorites.title) },
                    selected = state.selectedScreen == Screen.Favorites,
                    onClick = { 
                        viewModel.process(MainIntent.SelectScreen(Screen.Favorites))
                        analyticsVm.process(AnalyticsIntent.Log("tab_favorites"))
                    }
                )
                
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Screen.Settings.icon,
                            contentDescription = Screen.Settings.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(Screen.Settings.title) },
                    selected = state.selectedScreen == Screen.Settings,
                    onClick = { 
                        viewModel.process(MainIntent.SelectScreen(Screen.Settings))
                        analyticsVm.process(AnalyticsIntent.Log("tab_settings"))
                    }
                )
            }
        }
    ) { innerPadding ->
        when (state.selectedScreen) {
            is Screen.News -> {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    onToggleFavorite = { news ->
                        viewModel.process(MainIntent.ToggleFavorite(news))
                    },
                    favoriteNewsIds = state.favoriteNews.map { it.id }.toSet(),
                    onDownloadArticle = {},
                    downloadedArticleIds = emptySet()
                )
            }
            is Screen.Favorites -> {
                FavoritesScreen(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    favoriteNews = state.favoriteNews,
                    onRemoveFavorite = { newsId ->
                        viewModel.process(MainIntent.RemoveFavorite(newsId))
                    }
                )
            }
            
            is Screen.Settings -> {
                SettingsScreen(
                    modifier = Modifier.fillMaxSize(),
                    onLogout = onLogout
                )
            }
        }
    }
}

