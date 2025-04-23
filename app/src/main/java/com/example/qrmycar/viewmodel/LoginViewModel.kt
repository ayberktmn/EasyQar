package com.example.qrmycar.viewmodel

/*
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

   private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Kullanıcı giriş fonksiyonu
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

    // Kullanıcı girişini yapacak fonksiyon
    fun loginWithEmailPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    onSuccess()
                } else {
                    onFailure("Kullanıcı bulunamadı.")
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Error during login: ${e.message}")
                onFailure(e.message ?: "Bir hata oluştu.")
            }
        }
    }

    // Kullanıcı kaydedecek fonksiyon
    fun registerWithEmailPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    onSuccess()
                } else {
                    onFailure("Kullanıcı oluşturulamadı.")
                }
            } catch (e: Exception) {
                Log.e("RegisterError", "Error during registration: ${e.message}")
                onFailure(e.message ?: "Bir hata oluştu.")
            }
        }
    }
}





 */
