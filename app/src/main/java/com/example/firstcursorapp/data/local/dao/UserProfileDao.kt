package com.example.firstcursorapp.data.local.dao

import androidx.room.*
import com.example.firstcursorapp.data.local.entities.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    fun getUserProfile(userId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getUserProfileSync(userId: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Update
    suspend fun updateUserProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET lastSyncAt = :syncTime WHERE userId = :userId")
    suspend fun updateLastSyncTime(userId: String, syncTime: java.util.Date)

    @Query("UPDATE user_profiles SET preferredCategories = :categories WHERE userId = :userId")
    suspend fun updatePreferredCategories(userId: String, categories: List<String>)

    @Query("UPDATE user_profiles SET notificationPreferences = :preferences WHERE userId = :userId")
    suspend fun updateNotificationPreferences(userId: String, preferences: com.example.firstcursorapp.data.local.entities.NotificationPreferences)

    @Query("UPDATE user_profiles SET readingPreferences = :preferences WHERE userId = :userId")
    suspend fun updateReadingPreferences(userId: String, preferences: com.example.firstcursorapp.data.local.entities.ReadingPreferences)

    @Delete
    suspend fun deleteUserProfile(profile: UserProfile)

    @Query("DELETE FROM user_profiles WHERE userId = :userId")
    suspend fun deleteUserProfileById(userId: String)
}
