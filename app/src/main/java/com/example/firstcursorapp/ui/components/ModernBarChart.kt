package com.example.firstcursorapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ModernBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    maxBars: Int = 6,
    animationDuration: Int = 1000
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        return
    }
    
    val sortedData = data.sortedByDescending { it.second }.take(maxBars)
    val maxValue = sortedData.maxOfOrNull { it.second } ?: 1
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Vertical bar chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            sortedData.forEachIndexed { index, (action, count) ->
                VerticalBarItem(
                    action = action,
                    count = count,
                    maxCount = maxValue,
                    animationDuration = animationDuration,
                    delay = index * 100
                )
            }
        }
    }
}

@Composable
private fun VerticalBarItem(
    action: String,
    count: Int,
    maxCount: Int,
    animationDuration: Int,
    delay: Int = 0
) {
    val animatedHeight by animateFloatAsState(
        targetValue = if (maxCount > 0) count.toFloat() / maxCount.toFloat() else 0f,
        animationSpec = tween(durationMillis = animationDuration, delayMillis = delay),
        label = "barHeight"
    )
    
    Column(
        modifier = Modifier
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Value label at top
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Vertical bar
        Box(
            modifier = Modifier
                .width(40.dp)
                .height((120 * animatedHeight).dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
        )
    
        // Action name at bottom
        Text(
            text = action.replace("_", " ").replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 8.dp)
                .height(32.dp),
            maxLines = 2
        )
    }
}

