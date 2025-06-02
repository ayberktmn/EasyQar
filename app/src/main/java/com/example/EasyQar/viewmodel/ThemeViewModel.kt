package com.example.EasyQar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.EasyQar.AppTheme
import com.example.EasyQar.datastore.ThemeDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeDataStore: ThemeDataStore
) : ViewModel() {

    // StateFlow olarak tema
    private val _appTheme = MutableStateFlow(AppTheme.LIGHT)
    val appTheme: StateFlow<AppTheme> = _appTheme

    init {
        viewModelScope.launch {
            themeDataStore.getTheme().collect { savedTheme ->
                _appTheme.value = savedTheme
            }
        }
    }

    fun toggleTheme(newTheme: AppTheme) {
        viewModelScope.launch {
            themeDataStore.saveTheme(newTheme)  // DataStore'a kaydet
            _appTheme.value = newTheme           // Ve StateFlow'u güncelle
        }
    }
}