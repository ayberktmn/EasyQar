package com.example.EasyQar.viewmodel

import androidx.compose.runtime.staticCompositionLocalOf

val LocalThemeViewModel = staticCompositionLocalOf<ThemeViewModel> {
    error("No ThemeViewModel provided")
}
