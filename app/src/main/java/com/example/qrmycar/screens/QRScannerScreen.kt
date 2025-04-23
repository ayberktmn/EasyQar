package com.example.qrmycar.screens

import android.app.Activity
import android.content.Intent
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


@Composable
fun QRScannerScreen(navController: NavController) {
    var qrResult by remember { mutableStateOf<String?>(null) }

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

            QRScannerBox(
                modifier = Modifier
                    .size(350.dp) // Daha küçük kutu
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                onQrScanned = {
                    qrResult = it
                }
            )

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

    AndroidView(
        factory = { previewView },
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
    )

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val barcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalyzer.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        barcodes.firstOrNull()?.rawValue?.let { raw ->
                            try {
                                val decoded = Base64.decode(raw, Base64.DEFAULT)
                                val decodedString = String(decoded)
                                onQrScanned(decodedString)
                            } catch (e: Exception) {
                                // decoding hatası varsa işlemeyebilir
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

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
    }
}
