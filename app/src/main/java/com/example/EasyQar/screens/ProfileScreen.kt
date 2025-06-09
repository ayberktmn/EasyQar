package com.example.EasyQar.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.EasyQar.utils.CustomSmallTopAppBar
import com.example.EasyQar.R
import com.example.EasyQar.viewmodel.LoginViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    var profilResmi by remember { mutableStateOf<Uri?>(null) }
    var refreshKey by remember { mutableStateOf(0) } // Bu tetikleyici olacak

    val userEmail = loginViewModel.currentUserEmail ?: "Email bulunamadı"
    val adSoyad by loginViewModel.adSoyad.collectAsState(initial = null)
    val plate by loginViewModel.plate.collectAsState()


    // İlk veri yükleme
    LoadProfileData(
        context = context,
        loginViewModel = loginViewModel,
        onLoading = { isRefreshing = it },
        onImageLoaded = { profilResmi = it }
    )

    // İzin ve resim seçici
    val imageLauncher = rememberImagePickerLauncher(context) { uri ->
        uri?.let {
            saveImageToInternalStorage(context, it)?.let { file ->
                val savedUri = Uri.fromFile(file)
                profilResmi = savedUri

                // updateImage fonksiyonunu ViewModel'den çağır
                loginViewModel.updateImage()
            }
        }
    }

    val permissionLauncher = rememberPermissionLauncher(context, imageLauncher)


    Scaffold(topBar = { CustomSmallTopAppBar(title = "Profil") }) { innerPadding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true

                    // Dönme animasyonu 2 saniye görünsün
                    delay(1000)

                    profilResmi = getSavedProfileUri(context)

                    loginViewModel.loadAdSoyad()

                    // Sayfayı yeniden çiz
                    refreshKey++

                    isRefreshing = false
                }
            }
        ) {
            key(refreshKey) {
                ProfileContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    profilResmi = profilResmi,
                    adSoyad = adSoyad,
                    userEmail = userEmail,
                    plate = plate.toString(),
                    onImageClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    },
                    onEditProfileClick = { navController.navigate("profileEdit") },
                )
            }
        }
        // 🔄 Yükleme ekranı göster
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF1591EA))
            }
        }
    }
}

fun getSavedProfileUri(context: Context): Uri? {
    val path = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        .getString("profile_image_path", null)
    return path?.let { File(it).takeIf { file -> file.exists() }?.let { Uri.fromFile(it) } }
}

@Composable
private fun LoadProfileData(
    context: Context,
    loginViewModel: LoginViewModel,
    onLoading: (Boolean) -> Unit,
    onImageLoaded: (Uri?) -> Unit
) {
    LaunchedEffect(Unit) {
        onLoading(true)
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedPath = prefs.getString("profile_image_path", null)
        val profilResmi = savedPath?.let {
            val file = File(it)
            if (file.exists()) Uri.fromFile(file) else null
        }
        onImageLoaded(profilResmi)
        loginViewModel.loadAdSoyad()
        delay(500)
        onLoading(false)
    }
}

@Composable
private fun rememberImagePickerLauncher(
    context: Context,
    onResult: (Uri?) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent(),
    onResult = onResult
)

@Composable
private fun rememberPermissionLauncher(
    context: Context,
    imageLauncher: ManagedActivityResultLauncher<String, Uri?>
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        imageLauncher.launch("image/*")
    } else {
        Toast.makeText(context, "İzin verilmedi.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    profilResmi: Uri?,
    adSoyad: String?,
    userEmail: String,
    plate: String,
    onImageClick: () -> Unit,
    onEditProfileClick: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            ProfileCard(profilResmi, adSoyad, userEmail,plate, onImageClick)

            Spacer(modifier = Modifier.height(32.dp))

            ProfileMenuItem(
                icon = Icons.Default.Person,
                text = "Profil Bilgilerini Güncelle",
                onClick = onEditProfileClick
            )
            Spacer(modifier = Modifier.height(12.dp))


            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(id = R.string.version))
        }
    }
}

@Composable
private fun ProfileCard(
    profilResmi: Uri?,
    adSoyad: String?,
    userEmail: String,
    plate: String,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            LottieAnimationExample()

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (profilResmi != null) {
                    val painter = rememberAsyncImagePainter(
                        model = profilResmi,
                        imageLoader = LocalContext.current.imageLoader.newBuilder()
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .build()
                    )
                    Image(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .clickable { onImageClick() },
                        painter = painter,
                        contentScale = ContentScale.Crop,
                        contentDescription = "Seçilen Profil Resmi"
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.addprofilephoto),
                        contentDescription = "Profil Resmi",
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { onImageClick() }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    when (adSoyad) {
                        null -> {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Ad Soyad Yükleniyor...")
                        }

                        "Ad Soyad Bulunamadı" -> Text(
                            "Ad Soyad Bulunamadı",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        else -> Text(
                            text = adSoyad ?: "",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(36.dp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow(painter = painterResource(id = R.drawable.mail), label = "E-posta", value = userEmail)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            InfoRow(
                painter = painterResource(id = R.drawable.plate),
                label = "Plaka",
                value = plate
            )
        }
    }
}

@Composable
fun InfoRow(painter: Painter, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(35.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}


@Composable
private fun ProfileMenuItem(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    iconTint: Color = Color.Black,
    text: String,
    textColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = iconTint)
            } else if (iconPainter != null) {
                Icon(painter = iconPainter, contentDescription = null, tint = iconTint)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
    }
}



fun saveImageToInternalStorage(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "profile_image.jpg")

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        // Dosya yolu SharedPreferences'a kaydet
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("profile_image_path", file.absolutePath).apply()

        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun LottieAnimationExample() {
    // Lottie animasyonunun dosyasını yükleyin
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.caranimation))

    // Lottie animasyonunu görüntülemek için
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f), // Yükseklik ihtiyaca göre ayarlanabilir
            iterations = 1, // Sonsuz döngü
            speed = 1f // Hızı kontrol edebilirsiniz
        )
    }
}



