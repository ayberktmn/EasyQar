package com.example.EasyQar.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.EasyQar.utils.CustomSmallTopAppBar
import com.example.EasyQar.viewmodel.LoginViewModel
import com.example.EasyQar.viewmodel.ProfileEditViewModel

@Composable
fun ProfileEditScreen(
    navController: NavController,
    profileEditViewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val adSoyadState by profileEditViewModel.adSoyad.collectAsState()
    var adSoyad by remember { mutableStateOf("") }

    LaunchedEffect(adSoyadState) {
        adSoyad = adSoyadState ?: ""
    }

    val bloodTypeState by profileEditViewModel.bloodType.collectAsState()
    val diseasesState by profileEditViewModel.diseases.collectAsState()
    val medicationsState by profileEditViewModel.medications.collectAsState()
    val allergiesState by profileEditViewModel.allergies.collectAsState()

    // MutableState olarak tutuyoruz ki TextField güncellemeyi göstersin
    var bloodType by remember { mutableStateOf(bloodTypeState ?: "") }
    var diseases by remember { mutableStateOf(diseasesState ?: "") }
    var medications by remember { mutableStateOf(medicationsState ?: "") }
    var allergies by remember { mutableStateOf(allergiesState ?: "") }
    val primaryBlue = Color(0xFF1591EA)

    val isValidName = adSoyad.length >= 3 && adSoyad.all { it.isLetter() || it.isWhitespace() }
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    LaunchedEffect(bloodTypeState) { bloodType = bloodTypeState ?: "" }
    LaunchedEffect(diseasesState) { diseases = diseasesState ?: "" }
    LaunchedEffect(medicationsState) { medications = medicationsState ?: "" }
    LaunchedEffect(allergiesState) { allergies = allergiesState ?: "" }

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
            // Ad Soyad
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

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tıbbi Bilgiler",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = bloodType,
                        onValueChange = {input ->
                            bloodType = input
                                .split(" ")
                                .joinToString(" ") { word ->
                                    word.lowercase().replaceFirstChar { it.uppercase() }
                                } },
                        label = { Text("Kan Grubu") },
                        placeholder = { Text("Örnek: A Rh+") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            focusedLabelColor = primaryBlue,
                            cursorColor = primaryBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = diseases,
                        onValueChange = { input ->
                            diseases = input
                                .split(" ")
                                .joinToString(" ") { word ->
                                    word.lowercase().replaceFirstChar { it.uppercase() }
                                }
                        },
                        label = { Text("Hastalıklar") },
                        placeholder = { Text("Örnek: Astım, Diyabet") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            focusedLabelColor = primaryBlue,
                            cursorColor = primaryBlue
                        )
                    )


                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = medications,
                        onValueChange = { input ->
                            medications = input
                                .split(" ")
                                .joinToString(" ") { word ->
                                    word.lowercase().replaceFirstChar { it.uppercase() }
                                }
                        },
                        label = { Text("Kullandığı İlaçlar") },
                        placeholder = { Text("Örnek: İnsülin, Ventolin") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            focusedLabelColor = primaryBlue,
                            cursorColor = primaryBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { input ->
                            allergies = input
                                .split(" ")
                                .joinToString(" ") { word ->
                                    word.lowercase().replaceFirstChar { it.uppercase() }
                                }
                        },
                        label = { Text("Alerjiler") },
                        placeholder = { Text("Örnek: Penisilin, Polen") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            focusedLabelColor = primaryBlue,
                            cursorColor = primaryBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    profileEditViewModel.updateMedicalInfo(
                        bloodType = bloodType,
                        diseases = diseases,
                        medications = medications,
                        allergies = allergies
                    )
                    // Burada ViewModel'e güncellemeleri gönderebilirsin
                    profileEditViewModel.updateName(adSoyad)
                    // Aynı şekilde diğer tıbbi bilgileri de ViewModel'de saklayabilirsin (yapılacak)
                    navController.popBackStack()
                },
                enabled = isValidName,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kaydet")
            }
        }
    }
}


