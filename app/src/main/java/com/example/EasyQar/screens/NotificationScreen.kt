package com.example.EasyQar.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.EasyQar.R
import com.example.EasyQar.datastore.NotificationItem
import com.example.EasyQar.datastore.NotificationType
import com.example.EasyQar.utils.CustomSmallTopAppBar
import com.example.EasyQar.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NotificationScreen(navController: NavController, viewModel: NotificationViewModel = viewModel()) {
    val notifications = viewModel.notifications

    var showConfirmDialog by remember { mutableStateOf(false) }

    // Sayfa açıldığında bildirimleri okundu olarak işaretle
    LaunchedEffect(Unit) {
        viewModel.markAllAsRead()
    }

    Scaffold(
        topBar = {
            CustomSmallTopAppBar(
                title = "Bildirimler",
                action = {
                    IconButton(
                        onClick = {
                            if (notifications.isNotEmpty()) {
                                showConfirmDialog = true
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Tüm Bildirimleri Sil",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = paddingValues.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(notifications) { notification ->
                NotificationCard(notification = notification)
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text(text = "Bildirimleri Sil") },
                text = { Text(text = "Tüm bildirimleri silmek istediğinize emin misiniz?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearAllNotifications()
                        showConfirmDialog = false
                    }) {
                        Text(
                            "Evet",
                            color = Color.Red
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text(
                            "İptal",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    }
}


@Composable
fun NotificationCard(notification: NotificationItem) {
    val iconRes = when (notification.type) {
        NotificationType.SUCCESS -> R.drawable.check
        NotificationType.WARNING -> R.drawable.warning
        NotificationType.INFO -> R.drawable.information
        NotificationType.ERROR -> R.drawable.deleteuser
    }

    val backgroundColor = when (notification.type) {
        NotificationType.SUCCESS -> MaterialTheme.colorScheme.secondaryContainer
        NotificationType.WARNING -> MaterialTheme.colorScheme.surface
        NotificationType.INFO -> MaterialTheme.colorScheme.surface
        NotificationType.ERROR ->  MaterialTheme.colorScheme.surface
    }


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = notification.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = notification.timeAgo,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


