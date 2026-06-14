package com.example.freshgoapp.ui.RecipeMenu

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeMenuScreen(
    selectedLanguage: String, // <-- Parameter Bahasa Ditambahkan
    onBackClick: () -> Unit,
    onSearchClick: (String) -> Unit
) {
    var ingredientQuery by remember { mutableStateOf("") }

    // Kamus Bahasa
    val titleText = if (selectedLanguage == "EN") "Recipe Menu" else "Menu Resep Makanan"
    val headerText = if (selectedLanguage == "EN") "Find Cooking Ideas" else "Cari Ide Memasak"
    val descText = if (selectedLanguage == "EN") "Type an ingredient in English (e.g., chicken, beef, egg) to search for recipes online." else "Ketik nama bahan berbahasa Inggris (contoh: chicken, beef, egg) untuk mencari resep masakan dari internet."
    val labelText = if (selectedLanguage == "EN") "Main Ingredient" else "Nama Bahan Utama"
    val placeholderText = if (selectedLanguage == "EN") "Example: chicken" else "Misal: chicken"
    val buttonText = if (selectedLanguage == "EN") "Search Online Recipe" else "Cari Resep Online"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = headerText, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = descText, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = ingredientQuery, onValueChange = { ingredientQuery = it },
                label = { Text(labelText) }, placeholder = { Text(placeholderText) },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { if (ingredientQuery.isNotBlank()) onSearchClick(ingredientQuery) },
                modifier = Modifier.fillMaxWidth().height(50.dp), enabled = ingredientQuery.isNotBlank()
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(buttonText)
            }
        }
    }
}