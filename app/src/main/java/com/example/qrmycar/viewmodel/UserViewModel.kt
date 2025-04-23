package com.example.qrmycar.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrmycar.util.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper,
) : ViewModel() {

    private val _userEmail = mutableStateOf("")
    val userEmail: State<String> = _userEmail

    private val _plateNumber = mutableStateOf("")
    val plateNumber: State<String> = _plateNumber

  //  private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _userEmail.value = sharedPreferencesHelper.getUserEmail() ?: ""
            _plateNumber.value = sharedPreferencesHelper.getPlateNumber() ?: ""
        }
    }

 /*   fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kayıt başarılı, kullanıcıyı sharedPreferences'e kaydet
                    saveUserData(email, "")
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

  */

    fun saveUserData(email: String, plate: String) {
        sharedPreferencesHelper.saveUserData(email, plate)
        _userEmail.value = email
        _plateNumber.value = plate

        // Veriyi hemen kontrol et
        val savedPlateNumber = sharedPreferencesHelper.getPlateNumber()
        Log.d("UserViewModel", "Saved Plate Number: $savedPlateNumber")
    }
}
