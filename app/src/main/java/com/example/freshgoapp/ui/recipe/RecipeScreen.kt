package com.example.freshgoapp.ui.recipe

import androidx.compose.foundation.clickable // <-- IMPORT BARU UNTUK FITUR KLIK
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.freshgoapp.domain.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    ingredientName: String,
    selectedLanguage: String,
    onBackClick: () -> Unit,
    onRecipeClick: (String) -> Unit, // <-- PARAMETER BARU UNTUK MENANGKAP ID RESEP
    viewModel: RecipeViewModel = viewModel()
) {
    LaunchedEffect(ingredientName) { viewModel.fetchRecipes(ingredientName) }
    val state by viewModel.uiState.collectAsState()

    val titleText = if (selectedLanguage == "EN") "Cooking Ideas:" else "Ide Memasak:"
    val loadingText = if (selectedLanguage == "EN") "Loading recipes..." else "Memuat resep..."

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$titleText $ingredientName", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentStatus = state) {
                is RecipeUiState.Loading -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(loadingText)
                    }
                }
                is RecipeUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentStatus.recipes) { recipe ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    // --- KABEL NAVIGASI MENUJU DETAIL DIPASANG DI SINI ---
                                    // Catatan: Jika properti ID di model Recipe-mu bernama lain (misal: idMeal),
                                    // silakan ganti 'recipe.id' di bawah ini menjadi 'recipe.idMeal'
                                    .clickable { onRecipeClick(recipe.id.toString()) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(model = recipe.imageUrl, contentDescription = null, modifier = Modifier.size(80.dp).padding(4.dp), contentScale = ContentScale.Crop)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = recipe.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
                is RecipeUiState.Error -> {
                    val errorMsg = if (selectedLanguage == "EN" && currentStatus.errorMessage.contains("Tidak ada")) "No recipes found for this ingredient." else currentStatus.errorMessage
                    Text(text = errorMsg, modifier = Modifier.align(Alignment.Center).padding(24.dp), fontSize = 16.sp, color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}