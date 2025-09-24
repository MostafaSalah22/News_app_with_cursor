package com.example.firstcursorapp.data.repository

import com.example.firstcursorapp.data.local.dao.AnalyticsDao
import com.example.firstcursorapp.data.local.dao.AnalyticsSummary
import com.example.firstcursorapp.data.local.entities.AnalyticsEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepository @Inject constructor(
    private val analyticsDao: AnalyticsDao
) {
    
    fun getAnalyticsEventsByUser(userId: String): Flow<List<AnalyticsEvent>> {
        return analyticsDao.getAnalyticsEventsByUser(userId)
    }
    
    suspend fun getAnalyticsSummaryByUser(userId: String): Map<String, Int> {
        val summaryList = analyticsDao.getAnalyticsSummaryByUser(userId)
        return summaryList.associate { it.action to it.total_count }
    }
    
    suspend fun logAction(userId: String, action: String) {
        val existingCount = analyticsDao.getActionCount(userId, action)
        
        if (existingCount > 0) {
            // Update existing action count
            analyticsDao.incrementActionCount(userId, action)
        } else {
            // Insert new action
            val event = AnalyticsEvent(
                userId = userId,
                action = action,
                count = 1
            )
            analyticsDao.insertAnalyticsEvent(event)
        }
    }
    
    suspend fun clearAnalyticsForUser(userId: String) {
        analyticsDao.clearAnalyticsForUser(userId)
    }
    
    suspend fun clearActionForUser(userId: String, action: String) {
        analyticsDao.clearActionForUser(userId, action)
    }
    
    suspend fun deleteAnalyticsEvent(event: AnalyticsEvent) {
        analyticsDao.deleteAnalyticsEvent(event)
    }
}
