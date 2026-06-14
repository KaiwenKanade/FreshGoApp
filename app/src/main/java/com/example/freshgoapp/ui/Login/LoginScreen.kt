package com.example.freshgoapp.ui.login

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshgoapp.ui.theme.PrimaryFigmaGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    selectedLanguage: String,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgot: () -> Unit, // <-- PARAMETER BARU UNTUK PINDAH LAYAR
    verifyLogin: suspend (String, String) -> Boolean
) {
    val txtWelcome = if (selectedLanguage == "EN") "Welcome Back" else "Selamat Datang"
    val txtSubtitle = if (selectedLanguage == "EN") "Please enter your credentials to continue" else "Silakan masukkan kredensial Anda untuk melanjutkan"
    val lblEmail = "Email"
    val phEmail = if (selectedLanguage == "EN") "Enter your email" else "Masukkan email Anda"
    val lblPin = if (selectedLanguage == "EN") "Password" else "Kata Sandi"
    val phPin = if (selectedLanguage == "EN") "Enter your password" else "Masukkan kata sandi"
    val txtRemember = if (selectedLanguage == "EN") "Remember me" else "Ingat saya"
    val txtForgot = if (selectedLanguage == "EN") "Forgot Password?" else "Lupa Kata Sandi?"
    val btnLogin = if (selectedLanguage == "EN") "Login" else "Masuk"
    val txtNew = if (selectedLanguage == "EN") "New to FreshGo?" else "Baru di FreshGo?"
    val btnCreate = if (selectedLanguage == "EN") "Create an Account" else "Buat Akun"

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("freshgo_prefs", Context.MODE_PRIVATE)

    var email by remember { mutableStateOf(sharedPreferences.getString("saved_email", "") ?: "") }
    var pin by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(sharedPreferences.getBoolean("remember_me", false)) }
    var isError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("FreshGo", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = PrimaryFigmaGreen)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryFigmaGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Eco, contentDescription = null, tint = PrimaryFigmaGreen, modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(txtWelcome, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                txtSubtitle,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(lblEmail, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; isError = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(phEmail, color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.LightGray) },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF9F9F9), focusedContainerColor = Color(0xFFF9F9F9), unfocusedBorderColor = Color.Transparent)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(lblPin, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it; isError = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(phPin, color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.LockOpen, null, tint = Color.LightGray) },
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF9F9F9), focusedContainerColor = Color(0xFFF9F9F9), unfocusedBorderColor = Color.Transparent)
                )
            }

            if (isError) {
                Text(
                    text = if (selectedLanguage == "EN") "Invalid Email or Password" else "Email atau Kata Sandi salah!",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checked, onCheckedChange = { checked = it }, colors = CheckboxDefaults.colors(checkedColor = PrimaryFigmaGreen))
                    Text(txtRemember, fontSize = 12.sp, color = Color.Gray)
                }

                // --- KINI MEMANGGIL STRATEGI NAVIGASI PINDAH HALAMAN KETIKA DIKLIK ---
                TextButton(onClick = onNavigateToForgot) {
                    Text(txtForgot, fontSize = 12.sp, color = PrimaryFigmaGreen, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val isValid = verifyLogin(email, pin)
                        if (isValid) {
                            val editor = sharedPreferences.edit()
                            if (checked) {
                                editor.putString("saved_email", email)
                                editor.putBoolean("remember_me", true)
                            } else {
                                editor.remove("saved_email")
                                editor.putBoolean("remember_me", false)
                            }
                            editor.apply()

                            onLoginSuccess()
                        } else {
                            isError = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryFigmaGreen)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(btnLogin, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = com.example.freshgoapp.R.drawable.ic_launcher_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                        Text("Daily freshness delivered.", color = Color.White, fontSize = 10.sp)
                        Text("Direct from the local farms", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(txtNew, fontSize = 12.sp, color = Color.Gray)
            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(0.6f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(btnCreate, color = Color.Black, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}