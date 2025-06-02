package com.example.EasyQar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.EasyQar.utils.CustomSmallTopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScreen(
    navController: NavController,
    email: String = "", // Varsayılan boş değer
    plateNumber: String = "" // Varsayılan boş değer
) {
    val tabs = listOf("QR Tara", "QR Göster")
    var selectedTabIndex by remember { mutableStateOf(0) }
    val primaryBlue = Color(0xFF1591EA)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CustomSmallTopAppBar(title = "QR İşlemleri")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // topBar yüksekliğine göre içerik aşağıda başlasın
        ) {
            // TabRow direkt TopBar'ın altına gelsin
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = primaryBlue
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTabIndex) {
                    0 -> {
                        QRScannerScreen(navController = navController) // NavController'ı doğru şekilde kullan
                    }

                    1 -> {
                        QRCodeScreen(userEmail = email) // QRCodeScreen'yi doğru şekilde çağır
                    }
                }
            }
        }
    }
}
