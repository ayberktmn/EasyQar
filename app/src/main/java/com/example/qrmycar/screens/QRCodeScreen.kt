package com.example.qrmycar.screens

import android.util.Log
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
import com.example.qrmycar.utils.generateUserQRCode
import com.example.qrmycar.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun QRCodeScreen(userEmail: String) {
    val userViewModel: UserViewModel = hiltViewModel()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current

    // QR kodu yalnızca UID ile oluşturuluyor
    val qrBitmap = remember(uid) {
        uid?.let { generateUserQRCode(it) }
    }

    val plateNumber by userViewModel.plateNumber
    val isLoading by userViewModel.isLoading


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Aracına Özel QR Kod", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        // UID varsa QR kodu göster
        qrBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "QR Kod",
                modifier = Modifier.size(350.dp)
            )
        }

        // Firebase Messaging token'ını al ve Firestore'a kaydet
        LaunchedEffect(Unit) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "FCM token alınamadı", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCM", "FCM Token: $token")
                userViewModel.saveFcmTokenToFirestore(token)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Plaka numarasını yazdır
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (!plateNumber.isNullOrEmpty()) {
                Text(text = plateNumber ?: "", style = MaterialTheme.typography.headlineLarge)
            } else {
                Text(text = "Plaka numarası bulunamadı", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

