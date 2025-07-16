package com.example.EasyQar.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.EasyQar.utils.CustomSmallTopAppBar
import com.example.EasyQar.R // doğru package import edilmeli!
import com.example.EasyQar.viewmodel.ProfileEditViewModel

@Composable
fun MedicalInfoScreen(
    navController: NavController,
    uid: String,
    profileEditViewModel: ProfileEditViewModel = hiltViewModel()
) {
    LaunchedEffect(uid) {
        profileEditViewModel.loadMedicalInfoForUser(uid)
    }

    val adSoyad by profileEditViewModel.adSoyad.collectAsState()
    val bloodType by profileEditViewModel.bloodType.collectAsState()
    val diseases by profileEditViewModel.diseases.collectAsState()
    val medications by profileEditViewModel.medications.collectAsState()
    val allergies by profileEditViewModel.allergies.collectAsState()

    Scaffold(
        topBar = {
            CustomSmallTopAppBar(title = "Tıbbi Bilgiler")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            MedicalInfoItem(
                icon = R.drawable.blood,
                title = "Kan Grubu",
                value = bloodType.takeUnless { it.isNullOrEmpty() } ?: "Bilinmiyor"
            )

            MedicalInfoItem(
                icon = R.drawable.redinfo,
                title = "Hastalıklar",
                value = diseases.takeUnless { it.isNullOrEmpty() } ?: "Yok"
            )

            MedicalInfoItem(
                icon = R.drawable.pill,
                title = "Kullandığı İlaçlar",
                value = medications.takeUnless { it.isNullOrEmpty() } ?: "Yok"
            )

            MedicalInfoItem(
                icon = R.drawable.attention,
                title = "Alerjiler",
                value = allergies.takeUnless { it.isNullOrEmpty() } ?: "Yok"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                    navController.context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Acil Ara",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "112'yi Ara", color = Color.White)
            }
        }
    }
}



@Composable
fun MedicalInfoItem(icon: Int, title: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 12.dp)
        )

        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = value, fontSize = 14.sp)
        }
    }
}
