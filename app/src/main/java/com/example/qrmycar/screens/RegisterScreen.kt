package com.example.qrmycar.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.qrmycar.viewmodel.UserViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel() // Hilt kullanıyorsan
) {
    var email by remember { mutableStateOf("") }
    var plateNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val primaryBlue = Color(0xFF1591EA)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Kayıt Ol", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-Posta") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    focusedLabelColor = primaryBlue,
                    cursorColor = primaryBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = plateNumber,
                onValueChange = { plateNumber = it },
                label = { Text("Plaka Numarası") }, // Plaka girişi
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    focusedLabelColor = primaryBlue,
                    cursorColor = primaryBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Şifre") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    focusedLabelColor = primaryBlue,
                    cursorColor = primaryBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Şifreyi Tekrarla") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    focusedLabelColor = primaryBlue,
                    cursorColor = primaryBlue
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            isLoading = true

                         /*   userViewModel.registerUser(email, password) { success, error ->
                                isLoading = false

                                if (success) {
                                    // Email ve plaka bilgilerini shared preferences'e KAYIT BAŞARILI OLUNCA kaydet
                                    userViewModel.saveUserData(email, plateNumber)

                                    // Bilgilendirici toast
                                    Toast.makeText(
                                        navController.context,
                                        "Kayıt başarılı! Plaka: $plateNumber",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // QR ekranına yönlendir
                                    navController.navigate("qrScreen?email=$email&plateNumber=$plateNumber") {
                                        popUpTo("register") { inclusive = true }
                                    }

                                } else {
                                    // Hata mesajı göster
                                    Toast.makeText(
                                        navController.context,
                                        error ?: "Kayıt sırasında hata oluştu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("RegisterError", error ?: "Unknown error")
                                }
                            }

                          */

                        } else {
                            Toast.makeText(navController.context, "Şifreler uyuşmuyor", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .width(260.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
                ) {
                    Text(text = "Kayıt Ol", style = TextStyle(fontSize = 20.sp))
                }
            }


            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
