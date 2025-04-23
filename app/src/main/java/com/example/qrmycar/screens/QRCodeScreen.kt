package com.example.qrmycar.screens

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrmycar.generateQRCode
import com.example.qrmycar.generateUniqueQRCode
import com.example.qrmycar.viewmodel.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun QRCodeScreen(userEmail: String) {
    // UserViewModel'i Hilt ile al
    val userViewModel: UserViewModel = hiltViewModel()

    // ViewModel'den plaka numarasını al
    val plateNumber = userViewModel.plateNumber.value

    // QR kodunu oluştur
    val qrBitmap = remember(userEmail + plateNumber) {
        generateUniqueQRCode(plateNumber, userEmail)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Aracına Özel QR Kod", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "QR Kod",
            modifier = Modifier.size(350.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Plaka numarasını doğru şekilde göster
        Text(
            text = if (plateNumber.isEmpty()) "Plaka numarası bulunamadı" else plateNumber,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
