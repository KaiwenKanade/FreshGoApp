package com.example.freshgoapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshgoapp.data.Local.InventoryItem
import com.example.freshgoapp.ui.theme.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    items: List<InventoryItem>,
    onBackClick: () -> Unit,
    onDeleteItems: (List<InventoryItem>) -> Unit,
    onEditItem: (Int) -> Unit,
    onItemClick: (Int) -> Unit
) {
    var selectedItems by remember { mutableStateOf(setOf<InventoryItem>()) }
    val isSelectionMode = selectedItems.isNotEmpty()

    // State untuk Kotak Dialog Bulk Delete
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    // ---> KOTAK DIALOG KONFIRMASI HAPUS BANYAK BARANG <---
    if (showBulkDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteDialog = false },
            title = { Text("Hapus Bahan", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus ${selectedItems.size} bahan yang dicentang?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteItems(selectedItems.toList())
                    selectedItems = emptySet()
                    showBulkDeleteDialog = false
                }) {
                    Text("Hapus", color = CriticalRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text("${selectedItems.size} Terpilih", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { selectedItems = emptySet() }) { Icon(Icons.Default.Close, contentDescription = "Batal") } },
                    actions = {
                        IconButton(onClick = { selectedItems = if (selectedItems.size == items.size) emptySet() else items.toSet() }) { Icon(Icons.Default.Checklist, contentDescription = "Pilih Semua") }
                        // Jika diklik, munculkan kotak dialog
                        IconButton(onClick = { showBulkDeleteDialog = true }) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = CriticalRed) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            } else {
                TopAppBar(
                    title = { Text("Semua Bahan", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        }
    ) { paddingValues ->

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗑️", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Daftar bahan kosong\nTambahkan bahan terlebih dahulu.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    val isSelected = selectedItems.contains(item)
                    SelectableInventoryCard(
                        item = item,
                        isSelected = isSelected,
                        onToggleSelection = { selectedItems = if (isSelected) selectedItems - item else selectedItems + item },
                        onEdit = { onEditItem(item.id) },
                        onDelete = { onDeleteItems(listOf(item)) },
                        onClick = {
                            if (isSelectionMode) {
                                selectedItems = if (isSelected) selectedItems - item else selectedItems + item
                            } else {
                                onItemClick(item.id)
                            }
                        }
                    )
                }
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
    onClick: () -> Unit
) {
    val daysLeft = remember(item.expiryDate) {
        TimeUnit.MILLISECONDS.toDays(item.expiryDate - System.currentTimeMillis()).toInt()
    }

    val statusColor = remember(daysLeft) {
        when {
            daysLeft > 10 -> PrimaryGreen
            daysLeft in 3..10 -> WarningAmber
            daysLeft == 2 -> CriticalRed
            daysLeft in 0..1 -> Color(0xFFB71C1C)
            else -> Color.Gray
        }
    }

    val statusText = remember(daysLeft) {
        when {
            daysLeft < 0 -> "!!! EXPIRED"
            daysLeft in 0..1 -> "! $daysLeft DAYS LEFT"
            else -> "$daysLeft DAYS LEFT"
        }
    }

    // State untuk Kotak Dialog Single Delete
    var showSingleDeleteDialog by remember { mutableStateOf(false) }

    // ---> KOTAK DIALOG KONFIRMASI HAPUS SATU BARANG <---
    if (showSingleDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showSingleDeleteDialog = false },
            title = { Text("Hapus ${item.name}", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus bahan ini dari inventaris?") },
            confirmButton = {
                TextButton(onClick = {
                    showSingleDeleteDialog = false
                    onDelete() // Eksekusi hapus jika ditekan Ya/Hapus
                }) {
                    Text("Hapus", color = CriticalRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSingleDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryFigmaGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

            // Kotak Centang (Checkbox)
            Checkbox(checked = isSelected, onCheckedChange = { onToggleSelection() }, colors = CheckboxDefaults.colors(checkedColor = PrimaryFigmaGreen))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("🍎")
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("${item.quantity} ${item.unit} • ${item.category}", color = Color.Gray, fontSize = 11.sp)
                Text(text = statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            // ---> PERBAIKAN: Menampilkan Icon Edit & Hapus (Tempat Sampah) secara langsung <---
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { showSingleDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = CriticalRed)
                }
            }
        }
    }
}