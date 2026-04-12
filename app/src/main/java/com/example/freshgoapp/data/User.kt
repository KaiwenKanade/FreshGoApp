package com.example.freshgoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val pin: String,
    val name: String,
    val gender: String,
    val address: String,
    val photoUri: String // Menyimpan path/URL foto
)