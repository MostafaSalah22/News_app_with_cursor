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
        // Chart title
        Text(
            text = "Top Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Simple bar chart using Boxes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sortedData.forEach { (action, count) ->
                SimpleBarItem(
                    action = action,
                    count = count,
                    maxCount = maxValue,
                    animationDuration = animationDuration
                )
            }
        }
    }
}

@Composable
private fun SimpleBarItem(
    action: String,
    count: Int,
    maxCount: Int,
    animationDuration: Int
) {
    val animatedWidth by animateFloatAsState(
        targetValue = if (maxCount > 0) count.toFloat() / maxCount.toFloat() else 0f,
        animationSpec = tween(durationMillis = animationDuration),
        label = "barWidth"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Action label
        Text(
            text = action.replace("_", " ").replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Bar
        Box(
            modifier = Modifier
                .weight(2f)
                .height(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedWidth)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Count
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(30.dp),
            textAlign = TextAlign.End
        )
    }
}

