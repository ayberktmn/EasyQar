package com.example.EasyQar.utils

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun NoInternetDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("İnternet Bağlantısı Yok") },
            text = { Text("Lütfen internet bağlantınızı kontrol edin.") },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Tamam")
                }
            }
        )
    }
}



