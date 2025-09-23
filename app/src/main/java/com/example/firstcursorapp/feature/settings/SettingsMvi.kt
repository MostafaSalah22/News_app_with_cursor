package com.example.firstcursorapp.feature.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Application.dataStore by preferencesDataStore(name = "user_prefs")

data class SettingsState(
    val isDarkTheme: Boolean = false,
    val totalReadingSeconds: Long = 0L,
    val notificationsBreaking: Boolean = false,
    val notificationsDaily: Boolean = false
)

sealed interface SettingsIntent {
    data object ToggleTheme : SettingsIntent
    data class AddReadingSeconds(val seconds: Long) : SettingsIntent
    data class SetNotificationsBreaking(val enabled: Boolean) : SettingsIntent
    data class SetNotificationsDaily(val enabled: Boolean) : SettingsIntent
}

class SettingsViewModel(
    private val application: Application
) : ViewModel() {

    private val THEME_DARK = booleanPreferencesKey("theme_dark")
    private val READ_SECONDS = longPreferencesKey("reading_seconds_total")
    private val NOTIF_BREAKING = booleanPreferencesKey("notif_breaking")
    private val NOTIF_DAILY = booleanPreferencesKey("notif_daily")

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init {
        viewModelScope.launch {
            application.dataStore.data
                .map { prefs ->
                    SettingsState(
                        isDarkTheme = prefs[THEME_DARK] ?: false,
                        totalReadingSeconds = prefs[READ_SECONDS] ?: 0L,
                        notificationsBreaking = prefs[NOTIF_BREAKING] ?: false,
                        notificationsDaily = prefs[NOTIF_DAILY] ?: false
                    )
                }
                .collect { loaded -> _state.value = loaded }
        }
    }

    fun process(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.ToggleTheme -> toggleTheme()
            is SettingsIntent.AddReadingSeconds -> addReadingSeconds(intent.seconds)
            is SettingsIntent.SetNotificationsBreaking -> setNotifBreaking(intent.enabled)
            is SettingsIntent.SetNotificationsDaily -> setNotifDaily(intent.enabled)
        }
    }

    private fun toggleTheme() {
        viewModelScope.launch {
            val current = application.dataStore.data.map { it[THEME_DARK] ?: false }.first()
            application.dataStore.edit { prefs ->
                prefs[THEME_DARK] = !current
            }
        }
    }

    private fun addReadingSeconds(seconds: Long) {
        if (seconds <= 0) return
        viewModelScope.launch {
            val current = application.dataStore.data.map { it[READ_SECONDS] ?: 0L }.first()
            application.dataStore.edit { prefs ->
                prefs[READ_SECONDS] = current + seconds
            }
        }
    }

    private fun setNotifBreaking(enabled: Boolean) {
        viewModelScope.launch {
            application.dataStore.edit { prefs ->
                prefs[NOTIF_BREAKING] = enabled
            }
        }
    }

    private fun setNotifDaily(enabled: Boolean) {
        viewModelScope.launch {
            application.dataStore.edit { prefs ->
                prefs[NOTIF_DAILY] = enabled
            }
        }
    }
}


