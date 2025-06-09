package com.example.EasyQar.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.EasyQar.datastore.NotificationItem
import com.example.EasyQar.datastore.NotificationType
import com.example.EasyQar.utils.FirebaseNotificationDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow

class NotificationViewModel : ViewModel() {
    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> = _notifications

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val currentUserEmail: String?
        get() = auth.currentUser?.email


    val currentUserId = MutableStateFlow<String?>(null)

    val unreadCount: Int
        get() =  notifications.count { !it.read }


    init {
        val user = FirebaseAuth.getInstance().currentUser
        currentUserId.value = user?.uid
        fetchNotifications()
    }

    private fun fetchNotifications() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING) // ← Bu önemli
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
            "type" to type.name
        )

        val db = FirebaseFirestore.getInstance()

      /*  // Gönderenin koleksiyonuna ekle
        db.collection("users")
            .document(senderUserId)
            .collection("notifications")
            .add(notification)

       */

        // Alan kişinin koleksiyonuna ekle
        db.collection("users")
            .document(receiverUserId)
            .collection("notifications")
            .add(notification)
    }
}

