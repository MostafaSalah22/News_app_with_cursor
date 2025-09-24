package com.example.firstcursorapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics_events")
data class AnalyticsEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val action: String,
    val timestamp: Long = System.currentTimeMillis(),
    val count: Int = 1
)
