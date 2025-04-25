package com.example.qrmycar.viewmodel


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

    fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

}



