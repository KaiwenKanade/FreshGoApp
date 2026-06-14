package com.example.freshgoapp.data.Local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update // <-- IMPORT BARU UNTUK EDIT
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)

    @Query("SELECT * FROM inventory_items ORDER BY expiryDate ASC")
    fun getAllItemsFefo(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    fun getItemById(id: Int): Flow<InventoryItem>

    @Query("DELETE FROM inventory_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Int)

    @Query("DELETE FROM inventory_items")
    suspend fun deleteAllItems()
}