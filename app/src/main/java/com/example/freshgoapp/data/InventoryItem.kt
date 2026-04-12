package com.example.freshgoapp.data // Sesuaikan dengan nama package Anda!

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val quantity: Double,
    val unit: String,
    val purchaseDate: Long,
    val expiryDate: Long
)