package com.example.EasyQar.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.EasyQar.datastore.ThemeDataStore
import com.example.EasyQar.util.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val THEME_PREFERENCES_NAME = "theme_preferences.preferences_pb"

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.dataStoreFile(THEME_PREFERENCES_NAME)
        }
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("qr_prefs", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesHelper(
        sharedPreferences: SharedPreferences
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(sharedPreferences)
    }


    @Provides
    @Singleton
    fun provideThemeDataStore(dataStore: DataStore<Preferences>): ThemeDataStore {
        return ThemeDataStore(dataStore)
    }
}
