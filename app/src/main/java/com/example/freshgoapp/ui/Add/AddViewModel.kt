package com.example.freshgoapp.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshgoapp.data.Local.InventoryDao
import com.example.freshgoapp.data.Local.InventoryItem
import kotlinx.coroutines.launch

class AddViewModel(private val dao: InventoryDao) : ViewModel() {

    fun saveItem(item: InventoryItem) {
        viewModelScope.launch {
            // Logika BREAD yang sempurna: Pisahkan Insert dan Update
            if (item.id == 0) {
                // ID 0 berarti data baru (Add)
                dao.insertItem(item)
            } else {
                // ID lebih dari 0 berarti data sudah ada (Edit)
                dao.updateItem(item)
            }
        }
    }
}