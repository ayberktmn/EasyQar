package com.example.EasyQar.screens

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
import androidx.camera.core.CameraControl
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.EasyQar.R
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.example.EasyQar.BuildConfig
import com.example.EasyQar.datastore.NotificationType
import com.example.EasyQar.viewmodel.NotificationViewModel

@Composable
fun QRScannerScreen(navController: NavController,NotificationviewModel: NotificationViewModel = viewModel()) {
    var qrResult by remember { mutableStateOf<String?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }  // Loading durumu
    val context = LocalContext.current
    var isFlashOn by remember { mutableStateOf(false) }

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
                        .size(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                    navController = navController,
                    onQrScanned = {
                        qrResult = it
                    },
                    isFlashOn = isFlashOn // yeni parametre
                )

                Spacer(modifier = Modifier.height(16.dp))

                IconButton(
                    onClick = { isFlashOn = !isFlashOn },
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    val iconPainter = painterResource(
                        id = if (isFlashOn) R.drawable.yellowtorch else R.drawable.bluetorch
                    )
                    Icon(
                        painter = iconPainter,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize()
                    )
                }

            } else {
                Text("Kamera izni verilmedi. Lütfen izin verin.", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            qrResult?.let {

               // Text("Sonuç: $it")
            }
        }

    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun QRScannerBox(
    modifier: Modifier = Modifier,
    navController: NavController,
    onQrScanned: (String) -> Unit,
    isFlashOn: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    var isScanningActive by remember { mutableStateOf(true) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var scannedUid by remember { mutableStateOf<String?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

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

                                    scannedUid = decodedString
                                    isScanningActive = false
                                    onQrScanned(decodedString)

                                    CoroutineScope(Dispatchers.Main).launch {
                                        try {
                                            getUserInfoFromUid(context, decodedString)
                                            isDialogVisible = true
                                        } catch (e: Exception) {
                                            Log.e("QRCodeError", "Kullanıcı bilgisi alınırken hata: ${e.message}")
                                            Toast.makeText(context, "Bir hata oluştu.", Toast.LENGTH_SHORT).show()
                                        }
                                    }

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

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalyzer
        )
        cameraControl = camera.cameraControl
    }

    LaunchedEffect(isFlashOn) {
        cameraControl?.enableTorch(isFlashOn)
    }

    if (isDialogVisible && scannedUid != null) {
        ShowOptionsDialog(
            context = context,
            uid = scannedUid!!,
            onDismiss = { isDialogVisible = false; isScanningActive = true },
            onOptionSelected = { token, option, context -> },
            onScanAgain = { isScanningActive = true },
            navController,
            notificationViewModel = NotificationViewModel()
        )
    }
}


@Composable
fun ShowOptionsDialog(
    context: Context,
    uid: String,
    onDismiss: () -> Unit,
    onOptionSelected: (String, String, Context) -> Unit,
    onScanAgain: () -> Unit,
    navController: NavController,
    notificationViewModel: NotificationViewModel
) {
    val currentUserId by notificationViewModel.currentUserId.collectAsState(initial = null)

    var selectedOption by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Bir Durum Seçin") },
        text = {
            Column {
                OptionItem("Yanlış Park", R.drawable.parkyasak, selectedOption) { selectedOption = it }
                OptionItem("Farlar Açık", R.drawable.faracik, selectedOption) { selectedOption = it }
                OptionItem("Cam Açık", R.drawable.camacik, selectedOption) { selectedOption = it }
                OptionItem("Kapılar Açık", R.drawable.kapiacik, selectedOption) { selectedOption = it }
                OptionItem("Kaza", R.drawable.kaza, selectedOption) { selectedOption = it }
                OptionItem("Kullanıcıyı Şikayet Et", R.drawable.megaphone, selectedOption) { selectedOption = it }
                OptionItem("Tıbbi Bilgiler", R.drawable.healthinsurance, selectedOption) {
                    selectedOption = it
                    if (it == "Tıbbi Bilgiler") {
                        // Önce uid'yi burada kullan (örneğin logla veya toast göster)
                        getUserInfoFromUid(context, uid)

                        // Sonra scannedUid olarak uid'yi kullanarak navigate et
                        val scannedUid = uid
                        navController.navigate("medicalInfo/$scannedUid")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedOption != null && currentUserId != null) {
                        notificationViewModel.viewModelScope.launch {
                            val token = getFcmTokenByUid(uid)
                            if (token != null) {
                                val title = "EasyQar"
                                val message = "Aracınızın durumu: $selectedOption"

                                // Bildirim gönder
                                sendPushNotificationToUser(token, title, message, context)

                                notificationViewModel.sendNotificationToTwoUsers(
                                    senderUserId = currentUserId!!,  // ViewModel'den alıyoruz
                                    receiverUserId = uid,
                                    title = title,
                                    message = message,
                                    type = NotificationType.WARNING
                                )

                                onOptionSelected(token, selectedOption!!, context)
                                onDismiss()
                                onScanAgain()
                            } else {
                                Toast.makeText(context, "Kullanıcının tokeni alınamadı", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Lütfen bir seçenek seçin", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(text = "Gönder", fontSize = 16.sp, color = colorResource(id = R.color.primaryBlue))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", fontSize = 16.sp, color = Color.Red)
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
            } else {
                Toast.makeText(context, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

suspend fun getFcmTokenByUid(uid: String): String? {
    return suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance().collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val token = document.getString("fcmToken")
                continuation.resume(token)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }
}

/* (eğer diğer fonk hata verirse bunu aktif et)
suspend fun getFcmTokenByUid(uid: String): String? = withContext(Dispatchers.IO) {
    val db = FirebaseFirestore.getInstance()
    try {
        val snapshot = db.collection("users").document(uid).get().await()
        if (snapshot.exists()) {
            return@withContext snapshot.getString("fcmToken") // Firestore'da token "fcmToken" adıyla tutuluyor varsayımı
        } else {
            Log.e("FCMToken", "Kullanıcı bulunamadı")
            return@withContext null
        }
    } catch (e: Exception) {
        Log.e("FCMToken", "Token alınırken hata: ${e.message}")
        return@withContext null
    }
}
 */

@Composable
fun OptionItem(
    label: String,
    iconRes: Int,
    selectedOption: String?,
    onSelect: (String) -> Unit
) {
    val isSelected = selectedOption == label

    val backgroundColor = when {
        isSelected && label == "Kullanıcıyı Şikayet Et" -> Color.Red
        isSelected -> colorResource(id = R.color.primaryBlue)
        else -> Color.Transparent
    }

    TextButton(
        onClick = { onSelect(label) },
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp)) // Önce clip
            .background(backgroundColor)     // Sonra background
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "$label icon",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(16.dp))

            val textColor = if (isSelected && label == "Kullanıcıyı Şikayet Et") {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant // veya onBackground
            }

            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}


// FCM bildirimini göndermek için asenkron işlem
suspend fun sendPushNotificationToUser(fcmToken: String, title: String, body: String, context: Context) {
    withContext(Dispatchers.IO) {
        try {
            // FCM yetkilendirme anahtarını alıyoruz
            val credentials = context.assets.open(BuildConfig.FCM_CREDENTIALS_FILE).use { inputStream ->
                GoogleCredentials.fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
            }

            credentials.refreshIfExpired()
            val accessToken = credentials.accessToken.tokenValue

            // JSON verisini hazırlıyoruz
            val json = """
                {
                  "message": {
                    "token": "$fcmToken",
                    "notification": {
                      "title": "$title",
                      "body": "$body"
                    }
                  }
                }
            """.trimIndent()

            // HTTP isteği gönderiyoruz
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/qrmycar-431dd/messages:send")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(json.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            // Yanıtı bekliyoruz
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("FCM", "Bildirim başarıyla gönderildi")
            } else {
                Log.e("FCM", "Bildirim gönderilemedi: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("FCM", "FCM bildirim hatası: ${e.message}")
        }
    }
}




