package com.example.qrmycar.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
                navController.navigate("qrScreen?email=${userEmail}&plateNumber=${plateNumber}") {
                    popUpTo("qrScreen") { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            selected = baseRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    popUpTo("profile") { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ayarlar") },
            label = { Text("Ayarlar") },
            selected = baseRoute == "settings",
            onClick = {
                navController.navigate("settings") {
                    popUpTo("settings") { inclusive = true }
                }
            }
        )
    }
}

