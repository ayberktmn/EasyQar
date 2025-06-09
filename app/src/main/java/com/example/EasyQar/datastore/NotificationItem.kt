package com.example.EasyQar.datastore

data class NotificationItem(
    val title: String,
    val description: String,
    val timeAgo: String,
    val type: NotificationType,
    val read: Boolean = false
)
