package com.example.freshgoapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshgoapp.data.InventoryItem
import com.example.freshgoapp.ui.theme.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    items: List<InventoryItem>,
    onBackClick: () -> Unit,
    onDeleteItems: (List<InventoryItem>) -> Unit,
    onEditItem: (InventoryItem) -> Unit,
    onItemClick: (Int) -> Unit // <-- KABEL NAVIGASI
) {
    var selectedItems by remember { mutableStateOf(setOf<InventoryItem>()) }
    val isSelectionMode = selectedItems.isNotEmpty()

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text("${selectedItems.size} Terpilih", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { selectedItems = emptySet() }) { Icon(Icons.Default.Close, contentDescription = "Batal") } },
                    actions = {
                        IconButton(onClick = { selectedItems = if (selectedItems.size == items.size) emptySet() else items.toSet() }) { Icon(Icons.Default.Checklist, contentDescription = "Pilih Semua") }
                        IconButton(onClick = { onDeleteItems(selectedItems.toList()); selectedItems = emptySet() }) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = CriticalRed) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1F8E9))
                )
            } else {
                TopAppBar(
                    title = { Text("Semua Bahan", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") } }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFFAFAFA)),
            contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                val isSelected = selectedItems.contains(item)
                SelectableInventoryCard(
                    item = item,
                    isSelected = isSelected,
                    onToggleSelection = { selectedItems = if (isSelected) selectedItems - item else selectedItems + item },
                    onEdit = { onEditItem(item) },
                    onDelete = { onDeleteItems(listOf(item)) },
                    onClick = {
                        // LOGIKA PINTAR:
                        if (isSelectionMode) {
                            selectedItems = if (isSelected) selectedItems - item else selectedItems + item // Ikut mencentang
                        } else {
                            onItemClick(item.id) // Buka detail jika tidak ada yang dicentang
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SelectableInventoryCard(
    item: InventoryItem,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit // <-- FUNGSI KLIK CARD
) {
    val daysLeft = TimeUnit.MILLISECONDS.toDays(item.expiryDate - System.currentTimeMillis()).toInt()
    val statusColor = when {
        daysLeft > 10 -> PrimaryGreen; daysLeft in 3..10 -> WarningAmber; daysLeft == 2 -> CriticalRed; daysLeft in 0..1 -> Color(0xFFB71C1C); else -> Color.Black
    }
    val statusText = when {
        daysLeft < 0 -> "!!! EXPIRED"; daysLeft in 0..1 -> "! $daysLeft DAYS LEFT"; else -> "$daysLeft DAYS LEFT"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }, // <-- BISA DIKLIK
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isSelected, onCheckedChange = { onToggleSelection() }, colors = CheckboxDefaults.colors(checkedColor = PrimaryFigmaGreen))
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) { Text("🍎") }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${item.quantity} ${item.unit} • ${item.category}", color = Color.Gray, fontSize = 11.sp)
                Text(text = statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            var expanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Menu") }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Edit Bahan") }, onClick = { expanded = false; onEdit() }, leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) })
                    DropdownMenuItem(text = { Text("Hapus", color = CriticalRed) }, onClick = { expanded = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = CriticalRed) })
                }
            }
        }
    }
}