package com.example.freshgoapp.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshgoapp.ui.theme.PrimaryFigmaGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Language", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // ---> FIX DARK MODE: Gunakan background dinamis <---
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Text("Choose your preferred language for the app interface.", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))

            LanguageOptionRow(
                label = "English",
                isSelected = currentLanguage == "EN",
                onClick = { onLanguageSelected("EN") }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

            LanguageOptionRow(
                label = "Bahasa Indonesia",
                isSelected = currentLanguage == "ID",
                onClick = { onLanguageSelected("ID") }
            )
        }
    }
}

@Composable
fun LanguageOptionRow(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,

            color = if (isSelected) PrimaryFigmaGreen else MaterialTheme.colorScheme.onBackground
        )
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryFigmaGreen)
        }
    }
}