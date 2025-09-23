package com.example.firstcursorapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val userId: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val preferredCategories: List<String> = emptyList(),
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),
    val readingPreferences: ReadingPreferences = ReadingPreferences(),
    val createdAt: Date = Date(),
    val lastSyncAt: Date = Date()
)

data class NotificationPreferences(
    val breakingNews: Boolean = false,
    val dailyDigest: Boolean = false,
    val digestTime: String = "08:00", // HH:mm format
    val categories: List<String> = emptyList(),
    val smartScheduling: Boolean = true
)

data class ReadingPreferences(
    val fontSize: Float = 16f,
    val darkMode: Boolean = false,
    val autoDownload: Boolean = false,
    val offlineMode: Boolean = false
)
