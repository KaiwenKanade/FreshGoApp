package com.example.freshgoapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshgoapp.data.Local.InventoryDao
import com.example.freshgoapp.data.Local.InventoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch // PENTING: Untuk memperbaiki error 'launch'

class HomeViewModel(private val dao: InventoryDao) : ViewModel() {

    // Mengambil data dengan urutan FEFO
    val inventoryItems: StateFlow<List<InventoryItem>> = dao.getAllItemsFefo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Fungsi READ (Satu Data)
    fun getInventoryItemById(id: Int): Flow<InventoryItem?> {
        return dao.getItemById(id)
    }

    // Fungsi DELETE (BREAD: Delete)
    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            dao.deleteItemById(itemId)
        }
    }
}