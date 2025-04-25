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

@Composable
fun QRScannerScreen(navController: NavController) {
    var qrResult by remember { mutableStateOf<String?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    val context =LocalContext.current

    // Kamera izni istemek için launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    // Kamera iznini kontrol et ve iste
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                hasCameraPermission = true
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

            if (hasCameraPermission) {
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
                Text("Sonuç: $it")
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
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        // QR kodu bulunduğunda ilk bulduğunda sonucu al
                        barcodes.firstOrNull()?.rawValue?.let { raw ->
                            try {
                                // QR kodunun çözülmesi
                                val decoded = Base64.decode(raw, Base64.DEFAULT)
                                val decodedString = String(decoded)
                                onQrScanned(decodedString) // Tarama sonucunu tetikle
                            } catch (e: Exception) {
                                // Hata durumunda işlem yapılmaz
                            }
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close() // Görüntüyü serbest bırak
                    }
            } else {
                imageProxy.close() // Görüntü null ise kapat
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
}
