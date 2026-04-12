package com.example.freshgoapp.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshgoapp.data.InventoryDao
import com.example.freshgoapp.data.InventoryItem
import kotlinx.coroutines.launch

class AddViewModel(private val dao: InventoryDao) : ViewModel() {
    fun saveItem(item: InventoryItem) {
        viewModelScope.launch {
            dao.insertItem(item)
        }
    }
}