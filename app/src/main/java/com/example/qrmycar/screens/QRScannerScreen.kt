package com.example.qrmycar.screens

import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import android.Manifest.permission.CAMERA
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.qrmycar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun QRScannerScreen(navController: NavController) {
    var qrResult by remember { mutableStateOf<String?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }  // Loading durumu
    val context = LocalContext.current


    // QR kodu okunduğunda gösterilecek dialog durumu
    var isDialogVisible by remember { mutableStateOf(false) }

    // Kamera izni istemek için launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
            isLoading = false  // İzin verildikten sonra loading durumu bitir
        }
    )

    // Kamera iznini kontrol et ve iste
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                hasCameraPermission = true
                isLoading = false  // İzin daha önce verilmişse loading durumu bitir
            }
            else -> {
                cameraPermissionLauncher.launch(CAMERA)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentSize()
        ) {
            Text("Aracın QR Kodunu Okutunuz", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Loading durumu kontrolü
            if (isLoading) {
                CircularProgressIndicator()  // Loading animasyonu
            } else if (hasCameraPermission) {
                QRScannerBox(
                    modifier = Modifier
                        .size(350.dp) // Daha küçük kutu
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                    onQrScanned = {
                        qrResult = it
                    }
                )
            } else {
                Text("Kamera izni verilmedi. Lütfen izin verin.", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            qrResult?.let {
                Text("Sonuç: $it")  // okunan qr a ait araba plakası çıksın yapılacak
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun QRScannerBox(
    modifier: Modifier = Modifier,
    onQrScanned: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    var isScanningActive by remember { mutableStateOf(true) }
    // QR kodu okunduktan sonra diyalog göstermek için durum
    var isDialogVisible by remember { mutableStateOf(false) }

    // AndroidView ile PreviewView'i bağlama
    AndroidView(
        factory = { previewView },
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
    )

    LaunchedEffect(Unit) {
        // Kamera sağlayıcısını alıyoruz
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        // Preview nesnesi
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        // Barcode scanner oluşturuluyor
        val barcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        // Image analysis (görüntü analizi) yapacak olan nesne
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Sadece en son görüntü üzerinde işlem yap
            .build()

        // Görüntü analizini başlatıyoruz
        imageAnalyzer.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            if (!isScanningActive) {
                imageProxy.close()
                return@setAnalyzer
            }

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        barcodes.forEach { barcode ->
                            barcode.rawValue?.let { raw ->
                                try {
                                    val decoded = Base64.decode(raw, Base64.DEFAULT)
                                    val decodedString = String(decoded)

                                    isScanningActive = false // ✅ taramayı durdur
                                    onQrScanned(decodedString)
                                    getUserInfoFromUid(context, decodedString)
                                    isDialogVisible = true
                                } catch (e: Exception) {
                                    Log.e("QRCodeError", "QR kodu çözülürken hata: ${e.message}")
                                    Toast.makeText(context, "QR kodu çözülürken bir hata oluştu.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }

        // Kamera seçimi
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll() // Önceki bağlanmış tüm kameraları temizle
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalyzer // Görüntü analizi kamera ile bağlanacak
        )
    }

    // QR kodu okunduktan sonra diyalog gösterme
    if (isDialogVisible) {
        ShowOptionsDialog(
            onDismiss = {
                isDialogVisible = false
                isScanningActive = true // ✅ taramayı yeniden başlat
            },
            onOptionSelected = { selectedOption ->
                Log.d("QRDialog", "Seçilen seçenek: $selectedOption")
            }
        )
    }
}


// Dialog'ı gösteren fonksiyon
@Composable
fun ShowOptionsDialog(
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Seçim Yapın") },
        text = {
            Column {
                TextButton(onClick = { onOptionSelected("Seçenek 1") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.parkyasak),
                            contentDescription = "Yanlış Park İkonu",
                            modifier = Modifier
                                .size(32.dp) // ikon boyutu
                                .clip(CircleShape) // yuvarlak şekil
                                .background(Color.White), // arka plan (isteğe bağlı)
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Yanlış Park",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black // İsteğe bağlı renk
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(onClick = { onOptionSelected("Seçenek 2") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.faracik),
                            contentDescription = "Farlar Açık İkonu",
                            modifier = Modifier
                                .size(32.dp) // ikon boyutu
                                .clip(CircleShape) // yuvarlak şekil
                                .background(Color.White), // arka plan (isteğe bağlı)
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Farlar Açık",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(onClick = { onOptionSelected("Seçenek 3") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.camacik),
                            contentDescription = "Cam Açık İkonu",
                            modifier = Modifier
                                .size(32.dp) // ikon boyutu
                                .clip(CircleShape) // yuvarlak şekil
                                .background(Color.White), // arka plan (isteğe bağlı)
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Cam Açık",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black // İsteğe bağlı renk
                            )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(onClick = { onOptionSelected("Seçenek 4") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.kapiacik),
                            contentDescription = "Kapılar Açık İkonu",
                            modifier = Modifier
                                .size(32.dp) // ikon boyutu
                                .clip(CircleShape) // yuvarlak şekil
                                .background(Color.White), // arka plan (isteğe bağlı)
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Kapılar Açık",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black // İsteğe bağlı renk
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(onClick = { onOptionSelected("Seçenek 5") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.kaza),
                            contentDescription = "Kaza İkonu",
                            modifier = Modifier
                                .size(32.dp) // ikon boyutu
                                .clip(CircleShape) // yuvarlak şekil
                                .background(Color.White), // arka plan (isteğe bağlı)
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Kaza",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black // İsteğe bağlı renk
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Kapat",
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }
        }
    )
}

fun getUserInfoFromUid(context: Context, uid: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .document(uid)  // QR kodundan alınan uid'yi kullanıyoruz
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val userName = document.getString("adSoyad")
                val plate = document.getString("plateNumber")

                // Veriyi logla
                Log.d("UserInfo", "adSoyad: $userName, plateNumber: $plate")

                if (userName != null && plate != null) {
                    Toast.makeText(context, "Ad: $userName - Plaka: $plate", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Ad veya Plaka bilgisi eksik.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
