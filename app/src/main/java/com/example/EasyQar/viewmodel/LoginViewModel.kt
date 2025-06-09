package com.example.EasyQar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _adSoyad = MutableStateFlow<String?>(null)
    val adSoyad: StateFlow<String?> get() = _adSoyad

    private val _plate = MutableStateFlow<String?>(null)
    val plate: StateFlow<String?> get() = _plate


    init {
        loadAdSoyad()
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    onResult(true, null)
                } else {
                    onResult(false, "Kullanıcı bulunamadı.")
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Error during login: ${e.message}")
                onResult(false, e.message ?: "Bir hata oluştu.")
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    fun loadAdSoyad() {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val userRef = firestore.collection("users").document(uid)

            viewModelScope.launch {
                try {
                    val documentSnapshot = userRef.get().await()
                    if (documentSnapshot.exists()) {
                        val fullName = documentSnapshot.getString("adSoyad") ?: "Ad Soyad Bulunamadı"
                        val plateNumber = documentSnapshot.getString("plateNumber") ?: "Plaka Bulunamadı"

                        _adSoyad.value = fullName
                        _plate.value = plateNumber

                        Log.d("Firestore", "Ad soyad: $fullName, Plaka: $plateNumber")
                    } else {
                        _adSoyad.value = "Ad Soyad Bulunamadı"
                        _plate.value = "Plaka Bulunamadı"
                        Log.d("Firestore", "Belge bulunamadı.")
                    }
                } catch (e: Exception) {
                    Log.e("Firestore", "Veri okuma hatası: ${e.message}")
                    _adSoyad.value = "Ad Soyad Bulunamadı"
                    _plate.value = "Plaka Bulunamadı"
                }
            }
        }
    }

    fun deleteAccount(onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        val uid = user?.uid

        if (user != null && uid != null) {
            viewModelScope.launch {
                try {
                    // Firestore'dan "users" koleksiyonundaki kullanıcı belgesini sil
                    firestore.collection("users").document(uid).delete().await()

                    // Firestore'dan "uniqueQr" koleksiyonundaki kullanıcıya ait belgeyi sil
                    firestore.collection("uniqueQr").document(uid).delete().await()

                    // Ardından Firebase Authentication'dan kullanıcıyı sil
                    user.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onResult(true, null) // Başarılı silme
                        } else {
                            onResult(false, task.exception?.message ?: "Kullanıcı silinemedi.")
                        }
                    }
                } catch (e: Exception) {
                    onResult(false, "Silme hatası: ${e.message}")
                }
            }
        } else {
            onResult(false, "Kullanıcı oturumu açık değil.")
        }
    }

    fun updateName(newName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        // Kullanıcı adı güncelleme
        firestore.collection("users").document(userId)
            .update("adSoyad", newName)
            .addOnSuccessListener {
                // Güncelleme başarılıysa bildirim ekle
                val notification = mapOf(
                    "title" to "Profil Güncellemesi",
                    "description" to "Ad Soyadınız $newName olarak güncellendi.",
                    "timestamp" to FieldValue.serverTimestamp(), // ← Doğru alan
                    "type" to "SUCCESS"
                )
                firestore.collection("users")
                    .document(userId)
                    .collection("notifications")
                    .add(notification)
            }
    }

    fun updateImage() {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val notification = mapOf(
                "title" to "Profil Güncellemesi",
                "description" to "Profil resminiz güncellendi.",
                "timestamp" to FieldValue.serverTimestamp(),
                "type" to "SUCCESS"
            )

            firestore.collection("users")
                .document(userId)
                .collection("notifications")
                .add(notification)
                .addOnSuccessListener {
                    Log.d("LoginViewModel", "Notification added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("LoginViewModel", "Error adding notification", e)
                }
        }
    }
}