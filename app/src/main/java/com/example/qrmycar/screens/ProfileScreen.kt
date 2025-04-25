package com.example.qrmycar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.qrmycar.CustomSmallTopAppBar

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CustomSmallTopAppBar(title = "Profil")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // topBar yüksekliğine göre içerik aşağıda başlasın
        ) {

        }
    }
}
