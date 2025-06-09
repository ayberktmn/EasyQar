package com.example.EasyQar.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.R.attr.onClick
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.EasyQar.AppTheme
import com.example.EasyQar.utils.CustomSmallTopAppBar
import com.example.EasyQar.viewmodel.LoginViewModel
import com.example.EasyQar.R
import com.example.EasyQar.viewmodel.LocalThemeViewModel

@Composable
fun SettingsScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()) {

    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val themeViewModel = LocalThemeViewModel.current
    val currentTheme by themeViewModel.appTheme.collectAsState()
    val isDarkMode = currentTheme == AppTheme.DARK
    val primaryBlue = colorResource(id = R.color.primaryBlue)

    val openLogoutDialog = remember { mutableStateOf(false) }
    val openDeleteDialog = remember { mutableStateOf(false) }

    val onDeleteAccountClick = { openDeleteDialog.value = true }
    val onLogoutClick = { openLogoutDialog.value = true }

    // Dialoglar
    LogoutDialog(
        openDialog = openLogoutDialog.value,
        onDismiss = { openLogoutDialog.value = false },
        onConfirm = {
            openLogoutDialog.value = false
            loginViewModel.logout()
            navController.navigate("login") {
                popUpTo(0)
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
                    navController.navigate("login") { popUpTo(0) }
                } else {
                    // Log hata
                }
            }
        }
    )

    Scaffold(
        topBar = {
            CustomSmallTopAppBar(title = "Ayarlar")
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Karanlık Mod
            item {
                SettingsItemWithToggle(
                    icon = painterResource(id = R.drawable.outline_dark_mode_24),
                    title = "Karanlık Mod",
                    isChecked = isDarkMode,
                    onToggle = {
                        val newTheme = if (isDarkMode) AppTheme.LIGHT else AppTheme.DARK
                        themeViewModel.toggleTheme(newTheme)
                    },
                    iconColor = primaryBlue
                )

            }

            // Bildirim Ayarlarına Gitmek İçin Intent

            val notificationIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            }

            // Bildirim Ayarları
            item {
                SettingsItemWithArrow(
                    icon = painterResource(id = R.drawable.notifications),
                    title = "Bildirim Ayarları",
                    onClick = {
                        context.startActivity(notificationIntent)
                    },
                    iconColor = primaryBlue
                )
            }

            // Gizlilik
            item {
                SettingsItemWithArrow(
                    icon = painterResource(id = R.drawable.shield),
                    title = "Gizlilik",
                    onClick = { /* Navigate to privacy */ },
                    iconColor = primaryBlue
                )
            }

            // Dil
            item {
                SettingsItemWithValue(
                    icon = painterResource(id = R.drawable.language),
                    title = "Dil",
                    value = "Türkçe",
                    onClick = { /* Navigate to language selection */ },
                    iconColor = primaryBlue
                )
            }

         //   item { Spacer(modifier = Modifier.height(8.dp)) }

            // Yardım & Destek
            item {
                SettingsItemWithArrow(
                    icon = painterResource(id = R.drawable.help),
                    title = "Yardım & Destek",
                    onClick = { /* Navigate to help */ },
                    iconColor = primaryBlue
                )
            }

            item{
                ProfileMenuItem(
                    iconPainter = painterResource(id = R.drawable.deleteuser),
                    text = "Hesabımı Sil",
                    textColor = Color.Red,
                    iconTint = Color.Red,
                    onClick = onDeleteAccountClick
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item{
               ProfileMenuItem(
                    icon = Icons.Default.ExitToApp,
                    text = "Çıkış Yap",
                    textColor = Color.Red,
                    iconTint = Color.Red,
                    onClick = onLogoutClick
               )
            }



        }
    }
}

@Composable
fun SettingsItemWithToggle(
    icon: Painter,
    title: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val primaryBlue = colorResource(id = R.color.primaryBlue)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isChecked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = primaryBlue,
                    checkedTrackColor = primaryBlue.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItemWithArrow(
    icon: Painter,
    title: String,
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    titleColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = titleColor,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItemWithValue(
    icon: Painter,
    title: String,
    value: String,
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = value,
                fontSize = 17.sp,
                color = Color(0xFF8E8E93)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(20.dp)
            )
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

