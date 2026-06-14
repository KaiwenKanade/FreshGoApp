package com.example.freshgoapp.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshgoapp.data.Local.User // Import User Database
import com.example.freshgoapp.ui.theme.PrimaryFigmaGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: User?, // Parameter untuk mengambil nama dari database
    isDarkTheme: Boolean, // Parameter untuk mengecek tema saat ini
    onThemeToggle: () -> Unit, // Fungsi ketika tombol bulan ditekan
    selectedLanguage: String,
    onLanguageNavigate: () -> Unit,
    onProfileNavigate: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FreshGo", fontWeight = FontWeight.Bold, color = PrimaryFigmaGreen) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PrimaryFigmaGreen) }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.Notifications, null, tint = PrimaryFigmaGreen) }

                    // TOMBOL DARK MODE
                    IconButton(onClick = onThemeToggle) {
                        // Ikon berubah jadi Matahari jika sedang mode gelap
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = PrimaryFigmaGreen
                        )
                    }

                    IconButton(onClick = onProfileNavigate) { Icon(Icons.Default.AccountCircle, null, tint = PrimaryFigmaGreen) }
                },
                // Gunakan warna surface dari tema agar bisa menyesuaikan dark mode
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        // Ganti Color.White menjadi MaterialTheme.colorScheme.background agar responsif Dark Mode
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            // Ganti Color.Black menjadi MaterialTheme.colorScheme.onBackground
            Text(
                text = if (selectedLanguage == "EN") "Settings" else "Pengaturan",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text("AKUN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryFigmaGreen, modifier = Modifier.padding(bottom = 8.dp))

            // MENGGUNAKAN NAMA DARI DATABASE
            SettingRow(
                icon = Icons.Default.Person,
                title = if (selectedLanguage == "EN") "My Profile" else "Profil Saya",
                subtitle = user?.name ?: "Guest" // Nama dinamis dari database!
            ) {
                IconButton(onClick = onProfileNavigate) { Icon(Icons.Default.ChevronRight, null, tint = Color.Gray) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("PREFERENSI UMUM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryFigmaGreen, modifier = Modifier.padding(bottom = 8.dp))

            SettingRow(
                icon = Icons.Default.Language,
                title = if (selectedLanguage == "EN") "Language" else "Bahasa",
                subtitle = if (selectedLanguage == "EN") "English" else "Bahasa Indonesia"
            ) {
                IconButton(onClick = onLanguageNavigate) { Icon(Icons.Default.ChevronRight, null, tint = Color.Gray) }
            }
        }
    }
}

@Composable
fun SettingRow(icon: ImageVector, title: String, subtitle: String, action: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryFigmaGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = PrimaryFigmaGreen)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
        action()
    }
}