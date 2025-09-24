package com.example.firstcursorapp.data.local.dao

import androidx.room.*
import com.example.firstcursorapp.data.local.entities.AnalyticsEvent
import kotlinx.coroutines.flow.Flow

data class AnalyticsSummary(
    val action: String,
    val total_count: Int
)

@Dao
interface AnalyticsDao {
    
    @Query("SELECT * FROM analytics_events WHERE userId = :userId")
    fun getAnalyticsEventsByUser(userId: String): Flow<List<AnalyticsEvent>>
    
    @Query("SELECT action, SUM(count) as total_count FROM analytics_events WHERE userId = :userId GROUP BY action")
    suspend fun getAnalyticsSummaryByUser(userId: String): List<AnalyticsSummary>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyticsEvent(event: AnalyticsEvent)
    
    @Query("UPDATE analytics_events SET count = count + 1 WHERE userId = :userId AND action = :action")
    suspend fun incrementActionCount(userId: String, action: String): Int
    
    @Query("SELECT COUNT(*) FROM analytics_events WHERE userId = :userId AND action = :action")
    suspend fun getActionCount(userId: String, action: String): Int
    
    @Query("DELETE FROM analytics_events WHERE userId = :userId")
    suspend fun clearAnalyticsForUser(userId: String)
    
    @Query("DELETE FROM analytics_events WHERE userId = :userId AND action = :action")
    suspend fun clearActionForUser(userId: String, action: String)
    
    @Delete
    suspend fun deleteAnalyticsEvent(event: AnalyticsEvent)
}
