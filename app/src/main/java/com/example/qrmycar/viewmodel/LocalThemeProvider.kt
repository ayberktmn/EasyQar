package com.example.qrmycar.viewmodel

import androidx.compose.runtime.staticCompositionLocalOf

val LocalThemeViewModel = staticCompositionLocalOf<ThemeViewModel> {
    error("No ThemeViewModel provided")
}
