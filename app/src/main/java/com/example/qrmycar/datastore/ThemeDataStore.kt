package com.example.qrmycar.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.qrmycar.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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

