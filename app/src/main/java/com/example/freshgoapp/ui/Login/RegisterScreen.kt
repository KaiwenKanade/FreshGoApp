package com.example.freshgoapp.ui.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions // Import tambahan
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType // Import tambahan
import androidx.compose.ui.text.input.PasswordVisualTransformation // Import tambahan
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.freshgoapp.data.Local.User
import com.example.freshgoapp.ui.theme.PrimaryFigmaGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    insertUser: suspend (User) -> Unit,
    clearInventory: suspend () -> Unit // Fungsi untuk mereset Home Screen
) {
    var email by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Laki-laki") }
    var address by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Launcher untuk memilih foto dari Galeri
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> photoUri = uri }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Buat Akun Baru", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pemilih Foto Profil
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5))
                    .clickable {
                        photoPickerLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Tambah Foto", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            // Kolom Password yang sudah disempurnakan
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Kata Sandi (Password)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), // Ubah huruf jadi titik-titik
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password) // Keyboard password
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender (Laki-laki/Perempuan)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        // 1. Simpan user ke Database
                        insertUser(User(email = email, pin = pin, name = name, gender = gender, address = address, photoUri = photoUri?.toString() ?: ""))
                        // 2. Kosongkan Home Screen (hapus data inventory lama)
                        clearInventory()
                        // 3. Pindah ke Home
                        onRegisterSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryFigmaGreen)
            ) {
                Text("Daftar & Masuk", fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = onBackToLogin, modifier = Modifier.padding(top = 8.dp)) {
                Text("Sudah punya akun? Login di sini", color = Color.Gray)
            }
        }
    }
}