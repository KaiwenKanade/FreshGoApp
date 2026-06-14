package com.example.freshgoapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshgoapp.data.Local.InventoryItem
import com.example.freshgoapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    item: InventoryItem,
    selectedLanguage: String,
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    val txtPurchaseDate = if (selectedLanguage == "EN") "PURCHASE DATE" else "TANGGAL BELI"
    val txtCategory = if (selectedLanguage == "EN") "CATEGORY" else "KATEGORI"
    val txtStorage = if (selectedLanguage == "EN") "Storage Notes" else "Catatan Penyimpanan"
    val txtEdit = if (selectedLanguage == "EN") "Edit" else "Ubah"
    val txtDelete = if (selectedLanguage == "EN") "Delete" else "Hapus"
    val txtShelfLife = if (selectedLanguage == "EN") "SHELF LIFE REMAINING" else "SISA MASA SIMPAN"
    val txtDaysRemaining = if (selectedLanguage == "EN") "Days Remaining" else "Hari Tersisa"
    val txtExpired = if (selectedLanguage == "EN") "Expired" else "Kadaluarsa"

    // --- OPTIMASI RAM: Simpan objek formatter berat ini ke dalam cache (remember) ---
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    val shelfLifeDays = TimeUnit.MILLISECONDS.toDays(item.expiryDate - System.currentTimeMillis())
    val isExpired = shelfLifeDays < 0

    val itemEmoji = when (item.category) {
        "Makanan", "Food" -> "🍱"
        "Minuman", "Drink" -> "🥤"
        "Sayur", "Veggie" -> "🥦"
        "Buah", "Fruit" -> "🍎"
        else -> "📦"
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FreshGo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = itemEmoji, fontSize = 100.sp)
            }

            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
                Text("${item.quantity} ${item.unit} • ${item.category}", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(24.dp))

                ShelfLifeCard(
                    shelfLifeDays = if (isExpired) 0L else shelfLifeDays,
                    isExpired = isExpired,
                    label = txtShelfLife,
                    subLabel = if (isExpired) txtExpired else txtDaysRemaining
                )

                Spacer(modifier = Modifier.height(32.dp))

                DetailRow(icon = Icons.Default.CalendarMonth, label = txtPurchaseDate, value = dateFormatter.format(Date(item.purchaseDate)))
                Spacer(modifier = Modifier.height(20.dp))
                DetailRow(icon = Icons.Default.Restaurant, label = txtCategory, value = item.category)

                Spacer(modifier = Modifier.height(32.dp))

                Text(txtStorage, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Simpan di tempat sejuk dan kering. Pastikan wadah tertutup rapat untuk menjaga kesegaran maksimal.",
                    color = Color.Gray, fontSize = 14.sp, lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = { onEditClick(item.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(txtEdit)
                    }

                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CriticalRed)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(txtDelete)
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(if (selectedLanguage == "EN") "Delete Item?" else "Hapus Bahan?") },
                text = { Text(if (selectedLanguage == "EN") "Are you sure you want to remove this item?" else "Apakah Anda yakin ingin menghapus bahan ini?") },
                confirmButton = {
                    TextButton(onClick = { onDeleteClick(item.id); showDeleteDialog = false }) {
                        Text(txtDelete, color = CriticalRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text(if (selectedLanguage == "EN") "Cancel" else "Batal") }
                }
            )
        }
    }
}

@Composable
fun ShelfLifeCard(shelfLifeDays: Long, isExpired: Boolean, label: String, subLabel: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpired) CriticalRed.copy(alpha = 0.1f) else PrimaryFigmaGreen.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = if (isExpired) CriticalRed else PrimaryFigmaGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(String.format("%02d", shelfLifeDays), fontWeight = FontWeight.Bold, fontSize = 72.sp, color = if (isExpired) CriticalRed else PrimaryFigmaGreen)
            Text(subLabel, color = if (isExpired) CriticalRed else PrimaryFigmaGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryFigmaGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)
        }
    }
}