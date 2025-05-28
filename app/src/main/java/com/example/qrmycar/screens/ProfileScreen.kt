package com.example.qrmycar.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.qrmycar.utils.CustomSmallTopAppBar
import com.example.qrmycar.R
import com.example.qrmycar.viewmodel.LoginViewModel
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val userEmail = loginViewModel.currentUserEmail ?: "Email bulunamadı"
    val adSoyad by loginViewModel.adSoyad.collectAsState(initial = null)

    val openLogoutDialog = remember { mutableStateOf(false) }
    val openDeleteDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Profil resmi Uri'si
    var profilResmi by remember { mutableStateOf<Uri?>(null) }

    // SharedPreferences'tan kayıtlı resmi oku
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedPath = prefs.getString("profile_image_path", null)
        savedPath?.let {
            val file = File(it)
            if (file.exists()) {
                profilResmi = Uri.fromFile(file)
            }
        }
    }

    // Resim seçici launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Resmi uygulama içi storage'a kaydet
            val savedFile = saveImageToInternalStorage(context, it)
            savedFile?.let { file ->
                profilResmi = Uri.fromFile(file)
            }
        }
    }

    // İzin isteği launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // İzin verildi, resim seçiciyi aç
            imageLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "İzin verilmedi.", Toast.LENGTH_SHORT).show()
        }
    }


    // Dialoglar
    LogoutDialog(
        openDialog = openLogoutDialog.value,
        onDismiss = { openLogoutDialog.value = false },
        onConfirm = {
            openLogoutDialog.value = false
            loginViewModel.logout()
            navController.navigate("login") {
                popUpTo(0) // geri stack'ini temizler
            }
        }
    )

    DeleteAccountDialog(
        openDialog = openDeleteDialog.value,
        onDismiss = { openDeleteDialog.value = false },
        onConfirm = {
            openDeleteDialog.value = false
            loginViewModel.deleteAccount { success, message ->
                if (success) {
                    // Kullanıcı silindi, login ekranına yönlendir veya mesaj göster
                    Log.e("DeleteAccount", message ?: "Kullanıcı silindi")
                    navController.navigate("login")
                } else {
                    // Hata mesajını göster
                    Log.e("DeleteAccount", message ?: "Bilinmeyen hata")
                }
            }

            navController.navigate("login") {
                popUpTo(0)
            }
        }
    )

    Scaffold(
        topBar = {
            CustomSmallTopAppBar(title = "Profil")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                    ) {

                        LottieAnimationExample()

                        // Profil ve bilgiler (resmin altına hizalanmış)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (profilResmi != null) {
                                val painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(context)
                                        .data(profilResmi)
                                        .diskCachePolicy(CachePolicy.DISABLED)
                                        .memoryCachePolicy(CachePolicy.DISABLED)
                                        .build()
                                )

                                Image(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                            } else {
                                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                            }
                                        },
                                    contentScale = ContentScale.Crop,
                                    painter = painter,
                                    contentDescription = "Seçilen Profil Resmi"
                                )
                            }
                            else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profil Resmi",
                                    tint = Color(0xFF1591EA),
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clickable {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                            } else {
                                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                            }
                                        }
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
                                    else ->
                                        Text(
                                        text = adSoyad ?: "",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.mail),
                                        contentDescription = "Email Icon",
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = userEmail,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("profileEdit") }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Profil Bilgilerini Güncelle", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openDeleteDialog.value = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painter = painterResource(id = R.drawable.deleteuser), contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Hesabımı Sil", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {  openLogoutDialog.value = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Çıkış Yap", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = stringResource(id = R.string.version))
            }
        }
    }
}


@Composable
fun LogoutDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Evet", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Hayır", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            title = { Text("Çıkış Yap") },
            text = { Text("Çıkış yapmak istediğinizden emin misiniz?") }
        )
    }
}


@Composable
fun DeleteAccountDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Evet, Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Vazgeç", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            title = { Text("Hesabı Sil") },
            text = { Text("Bu işlem geri alınamaz. Hesabınızı kalıcı olarak silmek istediğinizden emin misiniz?") }
        )
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
fun AnimatedBackgroundImage() {
    var visible by remember { mutableStateOf(false) }

    // Ekrana girince görünür yap
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -100 }), // Yukarıdan kayarak ve soluklaşarak gelsin
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Arka Plan Resmi",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f), // Yarıya kadar
            contentScale = ContentScale.Crop
        )
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



