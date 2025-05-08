package com.example.qrmycar.di

import android.content.Context
import android.content.SharedPreferences
import com.example.qrmycar.util.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
}
