package com.example.firstcursorapp

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import com.example.firstcursorapp.feature.settings.SettingsIntent
import com.example.firstcursorapp.feature.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun WebViewScreen(
    url: String,
    onBackClick: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    val settingsVm: SettingsViewModel = koinViewModel()
    var secondsCounter by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsCounter += 1
            if (secondsCounter % 5L == 0L) {
                settingsVm.process(SettingsIntent.AddReadingSeconds(5))
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // ✅ This prevents content overlap automatically
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Important: Prevent overlap with bars
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = true
                            displayZoomControls = false
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: android.webkit.WebResourceRequest?,
                                error: android.webkit.WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                isLoading = false
                            }
                        }

                        loadUrl(url)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            )

            if (isLoading) {
                WebViewShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun WebViewShimmer(modifier: Modifier = Modifier) {
    // New shimmer design: animated dots and flowing lines
    val transition: InfiniteTransition = rememberInfiniteTransition(label = "webview_shimmer_dots")
    
    val dot1Alpha = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(animation = tween(1200)),
        label = "dot1"
    )
    
    val dot2Alpha = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "dot2"
    )
    
    val lineFlow = transition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(animation = tween(2000)),
        label = "line_flow"
    )

    Box(modifier = modifier.background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            // Animated dots
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                repeat(3) { index ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                Color.LightGray.copy(
                                    alpha = if (index == 0) dot1Alpha.value else dot2Alpha.value
                                ),
                                androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }
            
            // Flowing line animation
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .offset(x = lineFlow.value.dp)
                        .background(
                            Color.LightGray.copy(alpha = 0.8f),
                            androidx.compose.foundation.shape.RoundedCornerShape(1.dp)
                        )
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
            
            // Simple loading text
            androidx.compose.material3.Text(
                text = "Loading article...",
                color = Color.LightGray,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        }
    }
}
