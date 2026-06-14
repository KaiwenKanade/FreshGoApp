package com.example.freshgoapp.domain.usecase

import com.example.freshgoapp.data.API.model.ApiConfig
import com.example.freshgoapp.domain.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetRecipesUseCase {
    suspend fun execute(ingredient: String): List<Recipe> {
        return withContext(Dispatchers.IO) {
            try {
                // Memanggil API untuk mencari resep berdasarkan nama bahan
                val response = ApiConfig.getApiService().searchRecipesByIngredient(ingredient)

                // Menerjemahkan data JSON (Meal) menjadi model UI (Recipe)
                response.meals?.map { meal ->
                    Recipe(
                        id = meal.idMeal,
                        title = meal.strMeal,
                        imageUrl = meal.strMealThumb
                    )
                } ?: emptyList()
            } catch (e: Exception) {
                // Jika tidak ada koneksi internet atau error, kembalikan daftar kosong
                emptyList()
            }
        }
    }
}