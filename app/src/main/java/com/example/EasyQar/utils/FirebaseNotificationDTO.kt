package com.example.EasyQar.utils

import com.example.EasyQar.datastore.NotificationItem
import com.example.EasyQar.datastore.NotificationType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FirebaseNotificationDTO(
    val title: String = "",
    val description: String = "",
    val timestamp: Date? = null,  // ← Buraya dikkat
    val type: String = ""
) {
    fun toDomain(): NotificationItem {
        val timeAgo = timestamp?.let { getTimeAgo(it) } ?: "Bilinmiyor"
        return NotificationItem(
            title = title,
            description = description,
            timeAgo = timeAgo,
            type = when (type.uppercase()) {
                "SUCCESS" -> NotificationType.SUCCESS
                "WARNING" -> NotificationType.WARNING
                else -> NotificationType.INFO
            }
        )
    }
}

fun getTimeAgo(date: Date): String {
    val now = System.currentTimeMillis()
    val diff = now - date.time

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "şimdi"
        minutes < 60 -> "$minutes dakika önce"
        hours < 24 -> "$hours saat önce"
        days < 7 -> "$days gün önce"
        else -> SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
    }
}

