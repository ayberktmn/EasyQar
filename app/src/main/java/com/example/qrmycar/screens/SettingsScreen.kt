package com.example.qrmycar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.qrmycar.utils.CustomSmallTopAppBar
import com.example.qrmycar.viewmodel.LoginViewModel

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
                .padding(innerPadding) // topBar yüksekliğine göre içerik aşağıda başlasın
        ) {
            TextButton(onClick = {
                // Çıkış yapma işlemi
                loginViewModel.logout()
                // Giriş ekranına yönlendir
                navController.navigate("login") // Yönlendirmek istediğiniz ekrana göre değiştirin
            }) {
                Text("Çıkış yap")
            }
        }
    }
}
