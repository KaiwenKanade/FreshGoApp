package com.example.freshgoapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // <-- UPDATE ICON
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
import com.example.freshgoapp.data.Local.InventoryItem
import com.example.freshgoapp.ui.theme.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    items: List<InventoryItem>,
    onBackClick: () -> Unit,
    onDeleteItems: (List<InventoryItem>) -> Unit,
    onEditItem: (InventoryItem) -> Unit,
    onItemClick: (Int) -> Unit
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
                    // FIX DARK MODE: Ubah warna latar header saat mode seleksi agar menyesuaikan tema
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
        LazyColumn(
            // FIX DARK MODE: Background utama menyesuaikan tema gelap/terang
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background),
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

@Composable
fun SelectableInventoryCard(
    item: InventoryItem,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    // --- OPTIMASI RAM: Mengunci kalkulasi waktu dan warna agar tidak diulang-ulang saat layar di-scroll ---
    val daysLeft = remember(item.expiryDate) {
        TimeUnit.MILLISECONDS.toDays(item.expiryDate - System.currentTimeMillis()).toInt()
    }

    val statusColor = remember(daysLeft) {
        when {
            daysLeft > 10 -> PrimaryGreen
            daysLeft in 3..10 -> WarningAmber
            daysLeft == 2 -> CriticalRed
            daysLeft in 0..1 -> Color(0xFFB71C1C)
            else -> Color.Gray // FIX DARK MODE: Gray lebih aman terbaca di mode gelap daripada Black
        }
    }

    val statusText = remember(daysLeft) {
        when {
            daysLeft < 0 -> "!!! EXPIRED"
            daysLeft in 0..1 -> "! $daysLeft DAYS LEFT"
            else -> "$daysLeft DAYS LEFT"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        // FIX DARK MODE: Warna Card dan Efek Seleksi dibuat dinamis
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryFigmaGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isSelected, onCheckedChange = { onToggleSelection() }, colors = CheckboxDefaults.colors(checkedColor = PrimaryFigmaGreen))

            // FIX DARK MODE: Box Emoji
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("🍎")
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                // FIX DARK MODE: Teks nama bahan
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("${item.quantity} ${item.unit} • ${item.category}", color = Color.Gray, fontSize = 11.sp)
                Text(text = statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            var expanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Edit Bahan") }, onClick = { expanded = false; onEdit() }, leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) })
                    DropdownMenuItem(text = { Text("Hapus", color = CriticalRed) }, onClick = { expanded = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = CriticalRed) })
                }
            }
        }
    }
}