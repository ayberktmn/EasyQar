package com.example.qrmycar.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.qrmycar.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Bu metod, Firebase'den gelen mesajları alır
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Bildirim başlığı ve içeriğini alıyoruz
        val notificationTitle = remoteMessage.notification?.title
        val notificationBody = remoteMessage.notification?.body

        // Bildirimi gösterme fonksiyonunu çağırıyoruz
        showNotification(notificationTitle, notificationBody)
    }

    private fun showNotification(title: String?, body: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Bildirim kanalını oluşturuyoruz
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel"
            val channelName = "My Notifications"
            val channelDescription = "Channel for My Firebase Notifications"

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription

            notificationManager.createNotificationChannel(channel)
        }

        // Bildirim yapılandırmasını oluşturuyoruz
        val notification = NotificationCompat.Builder(this, "my_channel")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(com.google.android.gms.base.R.drawable.common_full_open_on_phone)
            .setAutoCancel(true) // Bildirim tıklanarak silinsin
            .build()

        // Bildirimi gösteriyoruz
        notificationManager.notify(0, notification)
    }
}
