package com.example.firstcursorapp.data.local.converters

import androidx.room.TypeConverter
import com.example.firstcursorapp.data.local.entities.NotificationPreferences
import com.example.firstcursorapp.data.local.entities.ReadingPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromNotificationPreferences(value: NotificationPreferences): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toNotificationPreferences(value: String): NotificationPreferences {
        return gson.fromJson(value, NotificationPreferences::class.java) ?: NotificationPreferences()
    }

    @TypeConverter
    fun fromReadingPreferences(value: ReadingPreferences): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toReadingPreferences(value: String): ReadingPreferences {
        return gson.fromJson(value, ReadingPreferences::class.java) ?: ReadingPreferences()
    }
}
