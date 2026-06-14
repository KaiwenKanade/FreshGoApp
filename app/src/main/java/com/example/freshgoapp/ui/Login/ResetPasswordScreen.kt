package com.example.freshgoapp.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshgoapp.ui.theme.PrimaryFigmaGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    selectedLanguage: String,
    onBackToLogin: () -> Unit,
    updatePassword: suspend (String, String) -> Int // Fungsi dari UserDao
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val title = if (selectedLanguage == "EN") "Reset Password" else "Atur Ulang Sandi"
    val desc = if (selectedLanguage == "EN") "Enter your email and a new password." else "Masukkan email Anda dan kata sandi baru."

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FreshGo", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = PrimaryFigmaGreen) },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color.White).verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.LockReset, contentDescription = null, tint = PrimaryFigmaGreen, modifier = Modifier.size(80.dp).padding(top = 20.dp))
            Spacer(modifier = Modifier.height(16.dp))

            Text(title, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(desc, fontSize = 14.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(32.dp))

            // 1. Input Email
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text(if (selectedLanguage == "EN") "Registered Email" else "Email Terdaftar") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Input Sandi Baru
            OutlinedTextField(
                value = newPassword, onValueChange = { newPassword = it },
                label = { Text(if (selectedLanguage == "EN") "New Password" else "Sandi Baru") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Konfirmasi Sandi Baru
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text(if (selectedLanguage == "EN") "Confirm Password" else "Konfirmasi Sandi") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    if (email.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                        Toast.makeText(context, if (selectedLanguage == "EN") "Please fill all fields" else "Mohon isi semua kolom", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (newPassword != confirmPassword) {
                        Toast.makeText(context, if (selectedLanguage == "EN") "Passwords do not match" else "Kata sandi tidak cocok", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Eksekusi Update ke Database
                    coroutineScope.launch {
                        val result = updatePassword(email, newPassword)
                        if (result > 0) {
                            Toast.makeText(context, if (selectedLanguage == "EN") "Password Reset Successful!" else "Sandi Berhasil Diubah!", Toast.LENGTH_SHORT).show()
                            onBackToLogin() // Kembali ke layar login
                        } else {
                            Toast.makeText(context, if (selectedLanguage == "EN") "Email not found" else "Email tidak terdaftar", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryFigmaGreen)
            ) {
                Text(if (selectedLanguage == "EN") "Reset Password" else "Simpan Sandi Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}