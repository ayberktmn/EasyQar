package com.example.qrmycar

/*
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

fun getFCMTokenAndSaveToFirestore(userId: String) {
    FirebaseMessaging.getInstance().token
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Token alınamadı", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM Token", token)

            // Firestore'a kaydet
            val db = FirebaseFirestore.getInstance()
            val tokenMap = mapOf("fcmToken" to token)

            db.collection("users")
                .document(userId)
                .set(tokenMap)
                .addOnSuccessListener {
                    Log.d("Firestore", "Token başarıyla kaydedildi")
                }
                .addOnFailureListener {
                    Log.w("Firestore", "Token kaydedilirken hata oluştu", it)
                }
        }
}


 */