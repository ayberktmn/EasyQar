package com.example.EasyQar

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import com.example.EasyQar.viewmodel.ThemeViewModel
import com.example.EasyQar.viewmodel.LocalThemeViewModel
import com.example.EasyQar.ui.theme.QrMyCarTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.EasyQar.utils.ConnectivityObserver
import com.example.EasyQar.utils.NoInternetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    private lateinit var connectivityObserver: ConnectivityObserver
    private val REQUEST_CODE_NOTIFICATION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATION
                )
            }
        }

        connectivityObserver = ConnectivityObserver(this)
        connectivityObserver.register()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = "#1591EA".toColorInt(),
                darkScrim = "#000000".toColorInt()
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )

        setContent {
            val currentTheme by themeViewModel.appTheme.collectAsState()
            val isConnected by connectivityObserver.isConnected.collectAsState()
            var showNoInternetDialog by remember { mutableStateOf(!isConnected) }

            LaunchedEffect(isConnected) {
                if (!isConnected) {
                    showNoInternetDialog = true
                }
            }

            CompositionLocalProvider(LocalThemeViewModel provides themeViewModel) {
                QrMyCarTheme(darkTheme = currentTheme == AppTheme.DARK) {

                    if (showNoInternetDialog) {
                        NoInternetDialog(
                            showDialog = true,
                            onDismiss = {
                                showNoInternetDialog = false
                                lifecycleScope.launch {
                                    delay(5000L) // 5 saniye bekle
                                    if (!connectivityObserver.isConnected.value) {
                                        showNoInternetDialog = true
                                    }
                                }
                            }
                        )
                    }
                    Navigation()
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        connectivityObserver.unregister()
    }
}


