package com.example.EasyQar.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.EasyQar.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeDataStore(private val dataStore: DataStore<Preferences>) {

    private val THEME_KEY = stringPreferencesKey("app_theme")

    fun getTheme(): Flow<AppTheme> = dataStore.data
        .map { preferences ->
            when(preferences[THEME_KEY]) {
                "DARK" -> AppTheme.DARK
                else -> AppTheme.LIGHT
            }
        }

    suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}

