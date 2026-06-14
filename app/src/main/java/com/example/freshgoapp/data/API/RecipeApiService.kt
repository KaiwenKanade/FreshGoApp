package com.example.freshgoapp.data.API.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {

    @GET("filter.php")
    suspend fun searchRecipesByIngredient(
        @Query("i") ingredient: String
    ): RecipeResponse


    @GET("lookup.php")
    suspend fun getRecipeDetailById(
        @Query("i") recipeId: String
    ): RecipeDetailResponse
}


object ApiConfig {
    fun getApiService(): RecipeApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RecipeApiService::class.java)
    }
}