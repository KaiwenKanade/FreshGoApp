package com.example.freshgoapp.domain.usecase

import com.example.freshgoapp.data.API.model.ApiConfig
import com.example.freshgoapp.domain.model.RecipeDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetRecipeDetailUseCase {
    suspend fun execute(recipeId: String): RecipeDetail? {
        return withContext(Dispatchers.IO) {
            try {
                // Memanggil API detail resep (menggunakan look-up by id)
                val response = ApiConfig.getApiService().getRecipeDetailById(recipeId)
                val mealDetail = response.meals?.firstOrNull()

                if (mealDetail != null) {
                    RecipeDetail(
                        id = mealDetail.idMeal,
                        title = mealDetail.strMeal,
                        imageUrl = mealDetail.strMealThumb,
                        instructions = mealDetail.strInstructions ?: "Tidak ada instruksi tersedia.",
                        ingredients = mealDetail.getIngredientsList()
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}