package com.example.EasyQar.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.EasyQar.utils.CustomSmallTopAppBar
import com.example.EasyQar.viewmodel.LoginViewModel

@Composable
fun ProfileEditScreen (
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    var adSoyad by remember { mutableStateOf(loginViewModel.adSoyad.value ?: "") }
    val context = LocalContext.current
    var profilResmi by remember { mutableStateOf<Uri?>(null) }
    val primaryBlue = Color(0xFF1591EA)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profilResmi = uri
    }

    val isValidName = adSoyad.length >= 3 && adSoyad.all { it.isLetter() || it.isWhitespace() }


    Scaffold(
        topBar = {
            CustomSmallTopAppBar(title = "Profil Bilgileri")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = adSoyad,
                singleLine = true,
                onValueChange = {
                    adSoyad = it
                        .split(" ")
                        .joinToString(" ") { word ->
                            word.lowercase().replaceFirstChar { char -> char.uppercase() }
                        }
                },
                label = { Text("Ad Soyad") },
                modifier = Modifier.fillMaxWidth(),
                isError = adSoyad.isNotEmpty() && !isValidName,
                supportingText = {
                    if (adSoyad.isNotEmpty() && !isValidName) {
                        Text("En az 3 harf girilmeli ve sadece harf içermeli")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    focusedLabelColor = primaryBlue,
                    cursorColor = primaryBlue
                )
            )


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    loginViewModel.updateName(adSoyad)
                    navController.popBackStack()
                },
                enabled = isValidName,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                )
            ) {
                Text("Kaydet")
            }
        }
    }
}

