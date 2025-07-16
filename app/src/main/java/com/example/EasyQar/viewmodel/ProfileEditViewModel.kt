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

        val updateMap = mapOf(
            "bloodType" to bloodType,
            "diseases" to diseases,
            "medications" to medications,
            "allergies" to allergies
        )

        userRef.update(updateMap)
            .addOnSuccessListener {
                // Başarı durumunda bildirim ekleyebilirsin
                val notification = mapOf(
                    "title" to "Profil Güncellemesi",
                    "description" to "Tıbbi bilgileriniz güncellendi.",
                    "timestamp" to FieldValue.serverTimestamp(),
                    "type" to "SUCCESS"
                )
                userRef.collection("notifications").add(notification)
            }
            .addOnFailureListener { e ->
                // Hata durumunda log veya hata işlemi yapılabilir
                e.printStackTrace()
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
}
