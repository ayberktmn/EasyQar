package com.example.qrmycar.screens


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qrmycar.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val primaryBlue = Color(0xFF1591EA)

    // Kullanıcı giriş yapmış mı kontrol et ve yönlendir
    LaunchedEffect(Unit) {
        if (loginViewModel.isUserLoggedIn()) {
            navController.navigate("qrscreen") {
                // Clear the back stack and prevent going back to login screen
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

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
            Text(
                text = "Giriş Yap",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Kullanıcı Adı") },
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
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
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
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true
                            loginViewModel.loginUser(
                                email = username,
                                password = password
                            ) { success, error ->
                                isLoading = false
                                if (success) {
                                    navController.navigate("qrscreen") {
                                        // Clear the back stack and prevent going back to login screen
                                        popUpTo("login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } else {
                                    errorMessage = "Email ya da şifre yanlış"
                                }
                            }
                        } else {
                            errorMessage = "Email ve şifre boş olamaz"
                        }
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .width(260.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    enabled = username.isNotEmpty() && password.isNotEmpty()
                ) {
                    Text(text = "Giriş Yap", style = TextStyle(fontSize = 20.sp))
                }
            }


            // "Hesabınız yok mu? Kayıt ol" kısmı
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = buildAnnotatedString {
                    append("Hesabınız yok mu? ")
                    pushStyle(
                        SpanStyle(
                        color = primaryBlue, // Mavi renk
                        textDecoration = TextDecoration.Underline
                    )
                    )
                    append("Kayıt ol")
                    pop()
                },
                modifier = Modifier.clickable {
                    // Kayıt ol sayfasına yönlendir
                    navController.navigate("register")
                }
            )

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


