package com.example.EasyQar.util

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun saveUserData(email: String, plate: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_EMAIL, email)
            putString(KEY_PLATE_NUMBER, plate)
            apply()
        }
    }

    fun savePlateNumber(plate: String) {
        sharedPreferences.edit().putString(KEY_PLATE_NUMBER, plate).apply()
    }

    fun getPlateNumber(): String? {
        return sharedPreferences.getString(KEY_PLATE_NUMBER, null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    companion object {
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_PLATE_NUMBER = "plate_number"
    }
}
