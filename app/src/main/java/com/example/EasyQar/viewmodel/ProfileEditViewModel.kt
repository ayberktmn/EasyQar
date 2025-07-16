package com.example.EasyQar.viewmodel

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
class ProfileEditViewModel @Inject constructor() : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _adSoyad = MutableStateFlow<String?>(null)
    val adSoyad: StateFlow<String?> get() = _adSoyad

    private val _bloodType = MutableStateFlow<String?>(null)
    val bloodType: StateFlow<String?> get() = _bloodType

    private val _diseases = MutableStateFlow<String?>(null)
    val diseases: StateFlow<String?> get() = _diseases

    private val _medications = MutableStateFlow<String?>(null)
    val medications: StateFlow<String?> get() = _medications

    private val _allergies = MutableStateFlow<String?>(null)
    val allergies: StateFlow<String?> get() = _allergies

    init {
        loadMedicalInfo()
    }

    // Kullanıcının tıbbi bilgilerini Firestore'dan çek
    fun loadMedicalInfo() {
        val user = auth.currentUser
        val uid = user?.uid ?: return

        val userRef = firestore.collection("users").document(uid)

        viewModelScope.launch {
            try {
                val documentSnapshot = userRef.get().await()
                if (documentSnapshot.exists()) {
                    _adSoyad.value = documentSnapshot.getString("adSoyad") ?: ""
                    _bloodType.value = documentSnapshot.getString("bloodType") ?: ""
                    _diseases.value = documentSnapshot.getString("diseases") ?: ""
                    _medications.value = documentSnapshot.getString("medications") ?: ""
                    _allergies.value = documentSnapshot.getString("allergies") ?: ""
                } else {
                    _adSoyad.value = ""
                    _bloodType.value = ""
                    _diseases.value = ""
                    _medications.value = ""
                    _allergies.value = ""
                }
            } catch (e: Exception) {
                _adSoyad.value = ""
                _bloodType.value = ""
                _diseases.value = ""
                _medications.value = ""
                _allergies.value = ""
            }
        }
    }

    fun loadMedicalInfoForUser(uid: String) {
        val userRef = firestore.collection("users").document(uid)

        viewModelScope.launch {
            try {
                val documentSnapshot = userRef.get().await()
                if (documentSnapshot.exists()) {
                    _adSoyad.value = documentSnapshot.getString("adSoyad") ?: ""
                    _bloodType.value = documentSnapshot.getString("bloodType") ?: ""
                    _diseases.value = documentSnapshot.getString("diseases") ?: ""
                    _medications.value = documentSnapshot.getString("medications") ?: ""
                    _allergies.value = documentSnapshot.getString("allergies") ?: ""
                } else {
                    _adSoyad.value = ""
                    _bloodType.value = ""
                    _diseases.value = ""
                    _medications.value = ""
                    _allergies.value = ""
                }
            } catch (e: Exception) {
                _adSoyad.value = ""
                _bloodType.value = ""
                _diseases.value = ""
                _medications.value = ""
                _allergies.value = ""
            }
        }
    }

    // Kullanıcının tıbbi bilgilerini Firestore'a kaydet
    fun updateMedicalInfo(
        bloodType: String,
        diseases: String,
        medications: String,
        allergies: String
    ) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)

        viewModelScope.launch {
            try {
                val snapshot = userRef.get().await()

                val bloodTypeVal = snapshot.getString("bloodType") ?: ""
                val diseasesVal = snapshot.getString("diseases") ?: ""
                val medicationsVal = snapshot.getString("medications") ?: ""
                val allergiesVal = snapshot.getString("allergies") ?: ""

                if (
                    bloodTypeVal == bloodType &&
                    diseasesVal == diseases &&
                    medicationsVal == medications &&
                    allergiesVal == allergies
                ) {
                    // Değişiklik yok
                    return@launch
                }

// Firestore'u güncelle
                val updateMap = mapOf(
                    "bloodType" to bloodType,
                    "diseases" to diseases,
                    "medications" to medications,
                    "allergies" to allergies
                )

                userRef.update(updateMap).await()

// Bildirim ekle
                val notification = mapOf(
                    "title" to "Profil Güncellemesi",
                    "description" to "Tıbbi bilgileriniz güncellendi.",
                    "timestamp" to FieldValue.serverTimestamp(),
                    "type" to "SUCCESS"
                )
                userRef.collection("notifications").add(notification)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun updateName(newName: String) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)

        viewModelScope.launch {
            try {
                // Önce mevcut adı al
                val snapshot = userRef.get().await()
                val currentName = snapshot.getString("adSoyad") ?: ""

                // Eğer değişmemişse çık
                if (currentName == newName) {
                    return@launch
                }

                // Değişmişse Firestore'u güncelle
                userRef.update("adSoyad", newName).await()

                // Başarıyla güncellendiyse bildirim ekle
                val notification = mapOf(
                    "title" to "Profil Güncellemesi",
                    "description" to "Ad Soyadınız $newName olarak güncellendi.",
                    "timestamp" to FieldValue.serverTimestamp(),
                    "type" to "SUCCESS"
                )
                userRef.collection("notifications").add(notification)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
