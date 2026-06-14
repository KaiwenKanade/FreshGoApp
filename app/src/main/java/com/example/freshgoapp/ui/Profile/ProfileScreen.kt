package com.example.freshgoapp.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.freshgoapp.data.Local.User
import com.example.freshgoapp.ui.theme.PrimaryFigmaGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    selectedLanguage: String,
    onBackClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onPhotoChanged: (String) -> Unit // <-- PARAMETER BARU UNTUK MENYIMPAN FOTO
) {
    val initials = user?.name?.split(" ")?.joinToString("") { it.take(1) }?.take(2)?.uppercase() ?: "U"

    // ---> LOGIKA MEMBUKA GALERI HP <---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onPhotoChanged(uri.toString()) // Kirim URI foto baru ke database
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedLanguage == "EN") "My Profile" else "Profil Saya", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PrimaryFigmaGreen)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // ---> FIX DARK MODE <---
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---> FOTO PROFIL SEKARANG BISA DIKLIK <---
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(PrimaryFigmaGreen.copy(alpha = 0.1f))
                    .clickable {
                        // Buka galeri saat diklik
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!user?.photoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = user?.photoUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(initials, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = PrimaryFigmaGreen)
                }

                // Tambahan Ikon Edit transparan agar user tahu bisa diklik
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Photo", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FIX DARK MODE untuk Teks Nama
            Text(user?.name ?: "Guest", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(user?.email ?: "Belum ada email", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileInfoItem(Icons.Default.Email, "Email", user?.email ?: "-")
                HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 12.dp))

                ProfileInfoItem(Icons.Default.Person, "Gender", user?.gender ?: "-")
                HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 12.dp))

                ProfileInfoItem(Icons.Default.LocationOn, if (selectedLanguage == "EN") "Address" else "Alamat", user?.address ?: "-")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSignOutClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    text = if (selectedLanguage == "EN") "Sign Out" else "Keluar Akun",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF9F9F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PrimaryFigmaGreen)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            // FIX DARK MODE: Teks value
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}