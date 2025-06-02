package com.example.EasyQar.firebase

import com.google.firebase.firestore.FirebaseFirestore

fun sendMessageToUser(uid: String, message: String) {
    val db = FirebaseFirestore.getInstance()

    // Mesaj koleksiyonunu oluşturuyoruz
    val messagesRef = db.collection("messages")

    // Kullanıcıya mesaj gönderme
    val messageData = hashMapOf(
        "uid" to uid,
        "message" to message,
        "timestamp" to System.currentTimeMillis()
    )

    // Mesajı Firestore'a ekliyoruz
    messagesRef.add(messageData)
        .addOnSuccessListener {
            // Başarılıysa işlem yapılabilir
            println("Mesaj gönderildi!")
        }
        .addOnFailureListener { exception ->
            // Hata durumunda işlem yapılabilir
            println("Mesaj gönderilemedi: $exception")
        }
}
