package com.example.qrmycar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.qrmycar.CustomSmallTopAppBar
import com.example.qrmycar.viewmodel.LoginViewModel

@Composable
fun ProfileScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()) {
    val userEmail = loginViewModel.currentUserEmail ?: "Email bulunamadı"
    val adSoyad by loginViewModel.adSoyad

    Scaffold(
        topBar = {
            CustomSmallTopAppBar(title = "Profil")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Kart içinde Ad Soyad ve Email
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profil Fotoğrafı
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profil Resmi",
                            tint = Color(android.graphics.Color.parseColor("#1591EA")),
                            modifier = Modifier.size(96.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (adSoyad) {
                        null -> Text(
                            "Ad Soyad Yükleniyor...",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        "Ad Soyad Bulunamadı" -> Text(
                            "Ad Soyad Bulunamadı",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        else -> Text(
                            adSoyad!!,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // navController.navigate("edit_profile")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(android.graphics.Color.parseColor("#1591EA"))
                )
            ) {
                Text(
                    text = "Bilgileri Düzenle",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    // Çıkış işlemi
                    loginViewModel.logout()
                    // Giriş ekranına yönlendir
                    navController.navigate("login")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Çıkış Yap",
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}


