package com.example.freshgoapp.domain.model

data class RecipeDetail(
    val id: String,
    val title: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<String>
)