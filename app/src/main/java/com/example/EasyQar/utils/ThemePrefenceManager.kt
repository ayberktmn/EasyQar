package com.example.EasyQar.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.EasyQar.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemePreferenceManager(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_preference")
    }

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        when (preferences[THEME_KEY]) {
            AppTheme.DARK.name -> AppTheme.DARK
            else -> AppTheme.LIGHT
        }
    }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}
