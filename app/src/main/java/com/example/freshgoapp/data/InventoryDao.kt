package com.example.freshgoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

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