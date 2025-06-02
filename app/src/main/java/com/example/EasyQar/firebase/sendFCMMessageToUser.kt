package com.example.EasyQar.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage

fun sendFCMMessageToUser(fcmToken: String, message: String) {
    val message = RemoteMessage.Builder("$fcmToken@fcm.googleapis.com")
        .setMessageId("1")
        .addData("message", message)
        .build()

    FirebaseMessaging.getInstance().send(message)
}
