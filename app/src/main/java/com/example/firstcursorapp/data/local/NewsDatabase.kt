package com.example.firstcursorapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.firstcursorapp.data.local.converters.Converters
import com.example.firstcursorapp.data.local.dao.UserProfileDao
import com.example.firstcursorapp.data.local.dao.AnalyticsDao
import com.example.firstcursorapp.data.local.entities.UserProfile
import com.example.firstcursorapp.data.local.entities.AnalyticsEvent

@Database(
    entities = [
        UserProfile::class,
        AnalyticsEvent::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
