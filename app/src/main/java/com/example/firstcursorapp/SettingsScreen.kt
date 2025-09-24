package com.example.firstcursorapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import org.koin.androidx.compose.koinViewModel
import com.example.firstcursorapp.feature.analytics.AnalyticsViewModel
import com.example.firstcursorapp.feature.analytics.AnalyticsIntent
import com.example.firstcursorapp.ui.components.ModernBarChart
import com.example.firstcursorapp.feature.settings.SettingsIntent
import com.example.firstcursorapp.feature.settings.SettingsViewModel
import com.example.firstcursorapp.feature.auth.AuthViewModel
import com.example.firstcursorapp.feature.auth.AuthIntent
import androidx.compose.material3.Switch
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val authViewModel: AuthViewModel = koinViewModel()
    val analyticsViewModel: AnalyticsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val authState by authViewModel.state.collectAsState()
    val analyticsState by analyticsViewModel.state.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Theme row
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.DarkMode, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Dark Theme", style = MaterialTheme.typography.bodyLarge)
                        }
                        Switch(checked = state.isDarkTheme, onCheckedChange = {
                            viewModel.process(SettingsIntent.ToggleTheme)
                            analyticsViewModel.process(AnalyticsIntent.Log("toggle_theme"))
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "Reading",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Today, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        val minutes = state.totalReadingSeconds / 60
                        val seconds = state.totalReadingSeconds % 60
                        Text(text = "Total reading time: ${minutes}m ${seconds}s")
                    }
                }
            }

            // Profile Information
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    // User Name
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = authState.userProfile?.displayName ?: authState.currentUser?.displayName ?: "No name set",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    HorizontalDivider()
                    
                    // User Email
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Email, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = authState.currentUser?.email ?: "Not logged in",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    HorizontalDivider()
                    
                    // User ID
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "ID: ${authState.currentUser?.uid?.take(8) ?: "N/A"}...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Logout Section
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            authViewModel.process(AuthIntent.SignOut)
                            onLogout()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout")
                    }
                }
            }

            // Usage Analytics Chart
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "Usage Analytics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    if (analyticsState.events.isEmpty()) {
                        Text(
                            text = "No actions tracked yet. Start using the app to see your activity!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    } else {
                        val topActions = analyticsState.events.entries.sortedByDescending { it.value }.take(6)
                        ModernBarChart(
                            data = topActions.map { it.key to it.value },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.TextButton(
                                onClick = { analyticsViewModel.process(AnalyticsIntent.Clear) }
                            ) {
                                Text("Clear Data")
                            }
                        }
                    }
                }
            }

            // App Information
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "App Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "NEWS App v1.0.0",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    Text(
                        text = "Built with Jetpack Compose & Firebase",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ModernBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    chartHeight: Int = 200
) {
    if (data.isEmpty()) return
    val maxValue = (data.maxOf { it.second }.coerceAtLeast(1))
    // Resolve theme-dependent colors in composable scope (not inside Canvas)
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryColorTop = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
    val onPrimaryOutline = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.06f)
    
    Column(modifier = modifier) {
        // Vertical bars with labels at bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEachIndexed { index, (label, value) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.dp)
                ) {
                    // Value label at top of bar
                    val targetFraction = value.toFloat() / maxValue.toFloat()
                    val animatedFraction by animateFloatAsState(
                        targetValue = targetFraction,
                        animationSpec = tween(durationMillis = 800, delayMillis = index * 100),
                        label = "barAnim"
                    )
                    
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    // Bar container
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barWidth = size.width * 0.6f
                            val barHeight = size.height * animatedFraction
                            val top = size.height - barHeight
                            val corner = androidx.compose.ui.geometry.CornerRadius(x = 12f, y = 12f)
                            val brush = Brush.verticalGradient(
                                colors = listOf(
                                    primaryColorTop,
                                    primaryColor
                                ),
                                startY = 0f,
                                endY = size.height
                            )
                            drawRoundRect(
                                brush = brush,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    x = (size.width - barWidth) / 2f,
                                    y = top
                                ),
                                size = androidx.compose.ui.geometry.Size(width = barWidth, height = barHeight),
                                cornerRadius = corner
                            )
                            // subtle outline
                            drawRoundRect(
                                color = onPrimaryOutline,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    x = (size.width - barWidth) / 2f,
                                    y = top
                                ),
                                size = androidx.compose.ui.geometry.Size(width = barWidth, height = barHeight),
                                cornerRadius = corner,
                                style = Stroke(width = 1f)
                            )
                        }
                    }
                    
                    // Action name label at bottom
                    Text(
                        text = label,
                        maxLines = 2,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .height(32.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


