package com.example.EasyQar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.EasyQar.screens.*
import com.example.EasyQar.utils.BottomNavBar
import com.example.EasyQar.utils.RequestNotificationPermission
import com.example.EasyQar.viewmodel.UserViewModel

@Composable
fun Navigation() {
        val userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        val navController = rememberNavController()

        val isLoading by userViewModel.isLoading
        val userEmail by userViewModel.userEmail
        val plateNumber by userViewModel.plateNumber

        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route

        if (isLoading) {
            // Yalnızca loading ekranı göster
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Ana UI sadece yükleme bitince çizilir
            val startDestination = if (userEmail.isNotEmpty() && plateNumber?.isNotEmpty() == true) {
                "qrScreen?email=${userEmail}&plateNumber=${plateNumber}"
            } else {
                "login"
            }

            if (currentRoute == "login") {
                DisableBackPress()
            }

            Scaffold(
                bottomBar = {
                    if (currentRoute != "login" && currentRoute != "register") {
                        BottomNavBar(
                            navController = navController,
                            userEmail = userEmail,
                            plateNumber = plateNumber ?: ""
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable(
                        route = "qrScreen?email={email}&plateNumber={plateNumber}",
                        arguments = listOf(
                            navArgument("email") { defaultValue = "" },
                            navArgument("plateNumber") { defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        val plate = backStackEntry.arguments?.getString("plateNumber") ?: ""

                        QRScreen(
                            navController = navController,
                            email = email,
                            plateNumber = plate
                        )
                    }
                    composable("profile") { ProfileScreen(navController) }
                    composable("settings") { SettingsScreen(navController) }
                    composable("theme") { ThemeChangeScreen(navController) }
                    composable("notification") { NotificationScreen(navController) }
                    composable("profileEdit") { ProfileEditScreen(navController) }
                }
            }
        }
        RequestNotificationPermission()
}


@Composable
fun DisableBackPress() {
    BackHandler(enabled = true) {
        // Geri tuşu devre dışı
    }
}
