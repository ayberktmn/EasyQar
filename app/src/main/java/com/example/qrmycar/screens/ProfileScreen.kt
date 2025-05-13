package com.example.qrmycar.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.qrmycar.utils.CustomSmallTopAppBar
import com.example.qrmycar.R
import com.example.qrmycar.viewmodel.LoginViewModel

@Composable
fun ProfileScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()) {
    val userEmail = loginViewModel.currentUserEmail ?: "Email bulunamadı"
    val adSoyad by loginViewModel.adSoyad

    val openLogoutDialog = remember { mutableStateOf(false) }
    val openDeleteDialog = remember { mutableStateOf(false) }

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
                            .height(250.dp)
                    ) {
                        // Arka plan görseli (üst yarı)
                        Image(
                            painter = painterResource(id = R.drawable.bgcar),
                            contentDescription = "Arka Plan Resmi",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f), // Yarıya kadar
                            contentScale = ContentScale.Crop
                        )

                        // Profil ve bilgiler (resmin altına hizalanmış)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter) // YATAYDA ORTALA ve aşağı hizala
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center // Bu Row içindeki elemanları ortalamaya çalışır, ama align zaten yeterli
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profil Resmi",
                                tint = Color(0xFF1591EA),
                                modifier = Modifier.size(96.dp)
                            )

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
                                        adSoyad!!,
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
                            .clickable { /* Profil bilgileri */ }
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
                    Text("Hayır", color = Color.Black)
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
                    Text("Vazgeç", color = Color.Black)
                }
            },
            title = { Text("Hesabı Sil") },
            text = { Text("Bu işlem geri alınamaz. Hesabınızı kalıcı olarak silmek istediğinizden emin misiniz?") }
        )
    }
}


