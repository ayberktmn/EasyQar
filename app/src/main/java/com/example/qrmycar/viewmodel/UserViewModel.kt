package com.example.qrmycar.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmycar.util.SharedPreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    private val _userEmail = mutableStateOf("")
    val userEmail: State<String> = _userEmail

    private val _plateNumber = mutableStateOf<String?>(null)
    val plateNumber: State<String?> = _plateNumber

    private val _adSoyad = mutableStateOf("")
    val adSoyad: State<String> = _adSoyad


    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            _userEmail.value = user.email ?: ""
            loadPlateNumber(user.uid)
        } else {
            _userEmail.value = ""
            _plateNumber.value = null
            _adSoyad.value = ""
            _isLoading.value = false
        }
    }

    // Firebase Authentication + Firestore Kaydı tek fonksiyonda
    fun registerUser(
        email: String,
        adSoyad: String,
        plateNumber: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid ?: return@addOnCompleteListener

                    _userEmail.value = email

                    val userMap = hashMapOf(
                        "email" to email,
                        "adSoyad" to adSoyad,
                        "plateNumber" to plateNumber,
                        "uid" to uid
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            Log.d("UserViewModel", "Kullanıcı Firestore'a kaydedildi")

                            // 🔽 FCM Token'ı al ve kaydet
                            com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                                .addOnSuccessListener { token ->
                                    firestore.collection("users").document(uid)
                                        .update("fcmToken", token)
                                        .addOnSuccessListener {
                                            Log.d("UserViewModel", "FCM token kaydedildi")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("UserViewModel", "FCM token kaydedilemedi: ${e.message}")
                                        }
                                }

                            onResult(true, "Kayıt işlemi başarılı.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("UserViewModel", "Firestore kayıt hatası: ${e.message}")
                            onResult(false, "Firestore'a kaydedilirken hata oluştu: ${e.message}")
                        }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }


    fun loadPlateNumber(uid: String) {
        _isLoading.value = true

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val plate = document.getString("plateNumber")
                _adSoyad.value = document.getString("adSoyad") ?: ""
                _plateNumber.value = plate ?: ""
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Plaka yüklenirken hata oluştu", e)
                _plateNumber.value = ""
                _adSoyad.value = ""
                _isLoading.value = false
            }
    }

    /* fun saveFcmTokenToFirestore(token: String) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val docRef = firestore.collection("uniqueQr").document(user.uid)

            docRef.get()
                .addOnSuccessListener { document ->
                    val existingToken = document.getString("fcmToken")
                    if (existingToken == null || existingToken != token) {
                        val data = hashMapOf("fcmToken" to token)
                        docRef.set(data)
                            .addOnSuccessListener {
                                Log.d("UserViewModel", "Token kaydedildi.")
                            }
                            .addOnFailureListener { e ->
                                Log.e("UserViewModel", "Token kaydedilemedi: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("UserViewModel", "Token kontrol hatası: ${e.message}")
                }
        }
    }

     */
}
