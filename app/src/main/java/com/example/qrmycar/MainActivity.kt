package com.example.qrmycar

import android.graphics.Color
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.qrmycar.screens.QRScreen
import com.example.qrmycar.screens.ProfileScreen
import com.example.qrmycar.screens.SettingsScreen
import com.example.qrmycar.ui.theme.QrMyCarTheme
import androidx.navigation.navArgument
import com.example.qrmycar.screens.LoginScreen
import com.example.qrmycar.screens.RegisterScreen
import com.example.qrmycar.viewmodel.UserViewModel
import androidx.activity.SystemBarStyle
import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.toColorInt
import com.google.firebase.messaging.FirebaseMessaging

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = "#1591EA".toColorInt(), // Mavi renk
                darkScrim = "#000000".toColorInt() // Siyah renk
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )

        setContent {
            QrMyCarTheme {
                FirebaseMessaging.getInstance().isAutoInitEnabled = true
                Navigation()
            }
        }
    }
}
