package com.example.firstcursorapp

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.firstcursorapp.feature.home.HomeIntent
import com.example.firstcursorapp.feature.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import java.util.Locale
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import org.koin.androidx.compose.koinViewModel

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    onToggleFavorite: (com.example.firstcursorapp.data.model.NewsSource) -> Unit = {},
    favoriteNewsIds: Set<String> = emptySet(),
    onDownloadArticle: (com.example.firstcursorapp.data.model.NewsSource) -> Unit = {},
    downloadedArticleIds: Set<String> = emptySet()
) {
    val vm: HomeViewModel = koinViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.process(HomeIntent.Load) }

    val refreshing = state.isLoading
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = refreshing)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { vm.process(HomeIntent.Load) },
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
        // Top Bar
        HomeTopBar(vm = vm)
        
        // Category Filter Chips
        CategoryFilterChips(
            categories = state.availableCategories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = { category ->
                vm.process(HomeIntent.FilterByCategory(category))
            }
        )
        
        when {
            state.isLoading -> ShimmerListPlaceholder()
            !state.isOnline -> {
                // Simple offline message
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Internet Connection",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            state.errorMessage != null -> Text("Error: ${state.errorMessage}")
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.filteredSources) { s ->
                        ElevatedNewsCard(
                            imageUrl = s.icon,
                            title = s.name ?: "",
                            description = s.description ?: "",
                            onToggleFavorite = { onToggleFavorite(s) },
                            isFavorite = favoriteNewsIds.contains(s.id),
                            onDownload = {},
                            isDownloaded = false,
                            onClick = {
                                // Get the URL from the news source (try different fields)
                                val url = s.url ?: s.website ?: s.link
                                if (!url.isNullOrBlank() && navController != null) {
                                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("webview/$encodedUrl")
                                }
                            }
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun RowHeader(vm: HomeViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("EG") }
    val countries = remember {
        listOf("EG", "US")
            .map { code -> code to Locale("", code).displayCountry }
            .sortedBy { it.second }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Home", fontWeight = FontWeight.Bold)

        Box {
            TextButton(onClick = { expanded = !expanded }) {
                Text(countries.firstOrNull { it.first == selected }?.second ?: selected)
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .padding(end = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    countries.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text("$name ($code)") },
                            onClick = {
                                selected = code
                                expanded = false
                                vm.process(HomeIntent.Retry(country = code))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@androidx.compose.material3.ExperimentalMaterial3Api
private fun HomeTopBar(vm: HomeViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("EG") }
    val countries = remember {
        listOf("EG", "US")
            .map { code -> code to Locale("", code).displayCountry }
            .sortedBy { it.second }
    }

    TopAppBar(
        title = { Text(text = "Discover", fontWeight = FontWeight.Bold) },
        actions = {
            Box {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        text = countries.firstOrNull { it.first == selected }?.second ?: selected,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Select country")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        countries.forEach { (code, name) ->
                            DropdownMenuItem(
                                text = { Text("$name ($code)") },
                                onClick = {
                                    selected = code
                                    expanded = false
                                    vm.process(HomeIntent.Retry(country = code))
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ElevatedNewsCard(
    imageUrl: String?,
    title: String,
    description: String,
    onToggleFavorite: () -> Unit = {},
    isFavorite: Boolean = false,
    onDownload: () -> Unit = {},
    isDownloaded: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    contentScale = ContentScale.Crop
                )
                
                // Action buttons
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    
                    // Favorite button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                                tint = if (isFavorite) Color.Red else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (description.isNotBlank()) {
                    Text(
                        text = description,
                        modifier = Modifier.padding(top = 6.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmerListPlaceholder() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(6) {
            ShimmerCard()
        }
    }
}

@Composable
private fun ShimmerCard() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition: InfiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutLinearInEasing
            )
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RectangleShape)
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(18.dp)
                    .clip(RectangleShape)
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RectangleShape)
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
                    .clip(RectangleShape)
                    .background(brush)
            )
        }
    }
}

@Composable
private fun CategoryFilterChips(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    if (categories.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" chip
                FilterChip(
                    onClick = { onCategorySelected(null) },
                    label = { Text("All") },
                    selected = selectedCategory == null,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                // Category chips
                categories.forEach { category ->
                    FilterChip(
                        onClick = { onCategorySelected(category) },
                        label = { 
                            Text(
                                text = category.replaceFirstChar { 
                                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                                }
                            ) 
                        },
                        selected = selectedCategory == category,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}


