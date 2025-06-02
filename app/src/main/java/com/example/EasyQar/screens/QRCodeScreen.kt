package com.example.EasyQar.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.EasyQar.R
import com.example.EasyQar.utils.createBrandedQrBitmap
import com.example.EasyQar.utils.generateUserQRCode
import com.example.EasyQar.utils.saveBitmapToGallery
import com.example.EasyQar.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun QRCodeScreen(userEmail: String) {
    val userViewModel: UserViewModel = hiltViewModel()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val qrColor = android.graphics.Color.BLACK
    val backgroundColor = android.graphics.Color.WHITE

    val qrBitmap = remember(uid, isDarkTheme) {
        uid?.let { generateUserQRCode(it, qrColor, backgroundColor) }
    }

    val plateNumber by userViewModel.plateNumber
    val isLoading by userViewModel.isLoading
    val adSoyad by userViewModel.adSoyad

    var contentVisible by remember { mutableStateOf(false) }

    // Tüm veri yüklendi mi kontrolü
    val isReady = qrBitmap != null && !plateNumber.isNullOrEmpty()

    LaunchedEffect(Unit) {
        contentVisible = true
    }

    // Loading göster
    if (!isReady || isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // İçeriği göster
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Aracına Özel QR Kod",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Kod",
                        modifier = Modifier.size(280.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val brandedQrBitmap = createBrandedQrBitmap(
                        qrBitmap = it,
                        context = context,
                        logoResId = R.drawable.easyqartransparent,
                        appName = "EasyQar"
                    )

                    Button(
                        onClick = { saveBitmapToGallery(context, brandedQrBitmap) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.download),
                                contentDescription = "İndir",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("QR Kodunu İndir", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.emptyplate),
                        contentDescription = "Boş plaka",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f),
                        contentScale = ContentScale.Fit
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    ) {
                        Text(
                            text = plateNumber ?: "Plaka numarası bulunamadı",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
