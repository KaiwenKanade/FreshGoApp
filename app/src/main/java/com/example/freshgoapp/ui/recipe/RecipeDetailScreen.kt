package com.example.freshgoapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.freshgoapp.ui.theme.PrimaryFigmaGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    selectedLanguage: String,
    onBackClick: () -> Unit,
    viewModel: RecipeViewModel = viewModel() // <-- KONEKSI KE MESIN DATA DITAMBAHKAN
) {

    LaunchedEffect(recipeId) { viewModel.fetchRecipeDetail(recipeId) }
    val state by viewModel.detailUiState.collectAsState()

    val txtTitle = if (selectedLanguage == "EN") "Recipe Detail" else "Detail Resep"
    val txtIngredients = if (selectedLanguage == "EN") "Ingredients Needed" else "Bahan yang Dibutuhkan"
    val txtInstructions = if (selectedLanguage == "EN") "Instructions" else "Cara Memasak"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(txtTitle, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background)) {
            when (val currentStatus = state) {

                is RecipeDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryFigmaGreen)
                }


                is RecipeDetailUiState.Error -> {
                    Text(currentStatus.errorMessage, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                }


                is RecipeDetailUiState.Success -> {
                    val recipe = currentStatus.recipeDetail
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {


                        AsyncImage(
                            model = recipe.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(recipe.title, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = MaterialTheme.colorScheme.onBackground)
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(txtIngredients, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryFigmaGreen)
                            Spacer(modifier = Modifier.height(12.dp))


                            recipe.ingredients.forEach { ingredient ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryFigmaGreen, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(ingredient, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }


                            Spacer(modifier = Modifier.height(24.dp))
                            Text(txtInstructions, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryFigmaGreen)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(recipe.instructions, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground, lineHeight = 22.sp)
                        }
                    }
                }
            }
        }
    }
}