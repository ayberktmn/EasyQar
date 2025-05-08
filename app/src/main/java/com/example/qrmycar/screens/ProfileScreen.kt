package com.example.qrmycar.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profil Resmi",
                            tint = Color(0xFF1591EA),
                            modifier = Modifier.size(96.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

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
                                painter = painterResource(id = R.drawable.email),
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

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // navController.navigate("edit_profile")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1591EA)
                    )
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bilgileri Düzenle",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        openDeleteDialog.value = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Hesabımı Sil",
                        color = MaterialTheme.colorScheme.scrim,
                        fontSize = 18.sp,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        openLogoutDialog.value = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Çıkış Yap",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Versiyon: 1.0.0")
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


