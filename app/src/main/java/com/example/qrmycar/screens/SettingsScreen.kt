package com.example.qrmycar.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.qrmycar.utils.CustomSmallTopAppBar
import com.example.qrmycar.viewmodel.LoginViewModel
import com.example.qrmycar.R

@Composable
fun SettingsScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            CustomSmallTopAppBar(title = "Ayarlar")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Tema Değiştir
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Tema değiştir */ }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.drawable.theme),
                        contentDescription = null,
                        tint = Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Tema Değiştir", style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Bildirim Ayarları
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Bildirim ayarları */ }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Bildirim Ayarları", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
