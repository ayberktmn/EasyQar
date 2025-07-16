package com.example.EasyQar.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.EasyQar.viewmodel.NotificationViewModel

@Composable
fun BottomNavBar(
    navController: NavHostController,
    userEmail: String,
    plateNumber: String,
    viewModel: NotificationViewModel
) {
    val unreadCount by remember { derivedStateOf { viewModel.unreadCount } }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val baseRoute = currentRoute?.substringBefore("?") ?: ""

    NavigationBar {
        // QR
        NavigationBarItem(
            icon = {
                BottomNavIconWithLabel(
                    icon = { Icon(Icons.Default.Home, contentDescription = "QR") },
                    label = "QR",
                    selected = baseRoute == "qrScreen"
                )
            },
            selected = baseRoute == "qrScreen",
            onClick = {
                if (baseRoute != "qrScreen") {
                    navController.navigate("qrScreen?email=${userEmail}&plateNumber=${plateNumber}") {
                        popUpTo("qrScreen") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = androidx.compose.ui.graphics.Color.Transparent
            )
        )

        // Profil
        NavigationBarItem(
            icon = {
                BottomNavIconWithLabel(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = "Profil",
                    selected = baseRoute == "profile"
                )
            },
            selected = baseRoute == "profile",
            onClick = {
                if (baseRoute != "profile") {
                    navController.navigate("profile") {
                        popUpTo("profile") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = androidx.compose.ui.graphics.Color.Transparent
            )
        )

        // Ayarlar
        NavigationBarItem(
            icon = {
                BottomNavIconWithLabel(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ayarlar") },
                    label = "Ayarlar",
                    selected = baseRoute == "settings"
                )
            },
            selected = baseRoute == "settings",
            onClick = {
                if (baseRoute != "settings") {
                    navController.navigate("settings") {
                        popUpTo("settings") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = androidx.compose.ui.graphics.Color.Transparent
            )
        )

        // Bildirimler (rozetli)
        NavigationBarItem(
            icon = {
                BottomNavIconWithLabel(
                    icon = {
                        if (unreadCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ) {
                                        Text(
                                            text = unreadCount.toString(),
                                            color = MaterialTheme.colorScheme.onError,
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize
                                        )
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Bildirimler")
                            }
                        } else {
                            Icon(Icons.Default.Notifications, contentDescription = "Bildirimler")
                        }
                    },
                    label = "Bildirimler",
                    selected = baseRoute == "notification"
                )
            },
            selected = baseRoute == "notification",
            onClick = {
                if (baseRoute != "notification") {
                    navController.navigate("notification") {
                        popUpTo("notification") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = androidx.compose.ui.graphics.Color.Transparent
            )
        )
    }
}

// Seçiliyse icon biraz yukarı kayar, label görünür
// Seçili değilse icon ortada, label gizli (0 height)
@Composable
fun BottomNavIconWithLabel(
    icon: @Composable () -> Unit,
    label: String,
    selected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = if (selected) 4.dp else 8.dp)
    ) {
        // Iconu biraz yukarı kaydır seçili ise
        val iconModifier = if (selected) Modifier.padding(bottom = 2.dp) else Modifier

        Box(modifier = iconModifier) {
            icon()
        }

        if (selected) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        } else {
            // label gizli ama alan kaplamaması için height 0 yapabiliriz
            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}
