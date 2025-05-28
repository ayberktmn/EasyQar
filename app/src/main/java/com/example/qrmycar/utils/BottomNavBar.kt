package com.example.qrmycar.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(
    navController: NavHostController,
    userEmail: String,
    plateNumber: String
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val baseRoute = currentRoute?.substringBefore("?") ?: ""

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "QR") },
            label = { Text("QR") },
            selected = baseRoute == "qrScreen",
            onClick = {
                if (baseRoute != "qrScreen") {
                    navController.navigate("qrScreen?email=${userEmail}&plateNumber=${plateNumber}") {
                        popUpTo("qrScreen") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            selected = baseRoute == "profile",
            onClick = {
                if (baseRoute != "profile") {
                    navController.navigate("profile") {
                        popUpTo("profile") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ayarlar") },
            label = { Text("Ayarlar") },
            selected = baseRoute == "settings",
            onClick = {
                if (baseRoute != "settings") {
                    navController.navigate("settings") {
                        popUpTo("settings") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Bildirimler") },
            label = { Text("Bildirimler") },
            selected = baseRoute == "notification",
            onClick = {
                if (baseRoute != "notification") {
                    navController.navigate("notification") {
                        popUpTo("notification") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}


