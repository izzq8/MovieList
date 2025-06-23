package com.faizabhinaya.mymovielist2.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val PHOTO_URL = stringPreferencesKey("photo_url")
    }

    // Get user preferences flow
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                isDarkMode = preferences[DARK_MODE] ?: false,
                language = preferences[LANGUAGE] ?: "en",
                displayName = preferences[DISPLAY_NAME] ?: "",
                photoUrl = preferences[PHOTO_URL] ?: ""
            )
        }

    // Update dark mode setting
    suspend fun updateDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = isDarkMode
        }
    }

    // Update language setting
    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    // Update display name
    suspend fun updateDisplayName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[DISPLAY_NAME] = name
        }
    }

    // Update photo URL
    suspend fun updatePhotoUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PHOTO_URL] = url
        }
    }
}

// Data class to hold user preferences
data class UserPreferences(
    val isDarkMode: Boolean = false,
    val language: String = "en",
    val displayName: String = "",
    val photoUrl: String = ""
)
