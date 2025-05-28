package com.example.qrmycar.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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

    private fun loadAdSoyad() {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val userRef = firestore.collection("users").document(uid)

            viewModelScope.launch {
                try {
                    val documentSnapshot = userRef.get().await()
                    if (documentSnapshot.exists()) {
                        val fullName = documentSnapshot.getString("adSoyad") ?: "Ad Soyad Bulunamadı"
                        _adSoyad.value = fullName
                        Log.d("Firestore", "Ad soyad: $fullName")
                    } else {
                        _adSoyad.value = "Ad Soyad Bulunamadı"
                        Log.d("Firestore", "Belge bulunamadı.")
                    }
                } catch (e: Exception) {
                    Log.e("Firestore", "Veri okuma hatası: ${e.message}")
                    _adSoyad.value = "Ad Soyad Bulunamadı"
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

    fun updateName(adSoyad: String) {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val userRef = firestore.collection("users").document(uid)

            viewModelScope.launch {
                try {
                    userRef.update("adSoyad", adSoyad).await()
                    _adSoyad.value = adSoyad
                    Log.d("Firestore", "Ad soyad güncellendi: $adSoyad")
                } catch (e: Exception) {
                    Log.e("Firestore", "Ad soyad güncelleme hatası: ${e.message}")
                }
            }
        }
    }
}