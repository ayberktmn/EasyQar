package com.example.EasyQar.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.EasyQar.datastore.NotificationItem
import com.example.EasyQar.datastore.NotificationType
import com.example.EasyQar.utils.FirebaseNotificationDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow

class NotificationViewModel : ViewModel() {
    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> = _notifications

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var registrationListener: ListenerRegistration? = null

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    val currentUserId = MutableStateFlow<String?>(null)

    val unreadCount by derivedStateOf { _notifications.count { !it.read } }

    // AuthStateListener ekliyoruz:
    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val newUserId = firebaseAuth.currentUser?.uid
        currentUserId.value = newUserId
        fetchNotifications(newUserId)
    }

    init {
        // Mevcut kullanıcıya göre bildirimleri yükle
        val user = auth.currentUser
        currentUserId.value = user?.uid
        fetchNotifications(user?.uid)

        // Auth değişikliklerini dinle
        auth.addAuthStateListener(authListener)
    }

    // Parametreli fetchNotifications, böylece userId değiştiğinde çağrılır
    private fun fetchNotifications(userId: String?) {
        registrationListener?.remove()
        _notifications.clear()

        if (userId == null) return

        registrationListener = firestore.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                _notifications.clear()
                for (doc in snapshot.documents) {
                    val dto = doc.toObject(FirebaseNotificationDTO::class.java)
                    val item = dto?.toDomain()
                    if (item != null) {
                        _notifications.add(item)
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        registrationListener?.remove()
        auth.removeAuthStateListener(authListener)
    }

    // Diğer fonksiyonların aynen kalması yeterli
    fun sendNotificationToTwoUsers(
        senderUserId: String,
        receiverUserId: String,
        title: String,
        message: String,
        type: NotificationType
    ) {
        val notification = hashMapOf(
            "title" to title,
            "description" to message,
            "timestamp" to FieldValue.serverTimestamp(),
            "type" to type.name,
            "read" to false
        )

        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(receiverUserId)
            .collection("notifications")
            .add(notification)
    }

    fun markAllAsRead() {
        val userId = auth.currentUser?.uid ?: return

        val collectionRef = firestore.collection("users")
            .document(userId)
            .collection("notifications")

        collectionRef.get().addOnSuccessListener { snapshot ->
            for (doc in snapshot.documents) {
                if (doc.getBoolean("read") != true) {
                    doc.reference.update("read", true)
                }
            }
        }
    }

    fun clearAllNotifications() {
        val userId = auth.currentUser?.uid ?: return
        val collectionRef = firestore.collection("users")
            .document(userId)
            .collection("notifications")

        collectionRef.get().addOnSuccessListener { snapshot ->
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().addOnSuccessListener {
                _notifications.clear()
            }
        }
    }
}
