package com.example.freshgoapp.ui.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import com.example.freshgoapp.ui.theme.PrimaryFigmaGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    selectedLanguage: String,
    onSaveClick: (InventoryItem) -> Unit,
    onBackClick: () -> Unit
) {
    // --- KAMUS TEKS ---
    val txtTitle = if (selectedLanguage == "EN") "Add Item" else "Tambah Bahan"
    val txtCancel = if (selectedLanguage == "EN") "Cancel" else "Batal"
    val txtNameHint = if (selectedLanguage == "EN") "Item Name" else "Nama Bahan"
    val txtCategory = if (selectedLanguage == "EN") "Category" else "Kategori"
    val txtQuantity = if (selectedLanguage == "EN") "Quantity" else "Jumlah"
    val txtUnit = if (selectedLanguage == "EN") "Unit" else "Satuan"
    val txtDateBtn = if (selectedLanguage == "EN") "Expiry Date" else "Tanggal Kadaluarsa"
    val txtSaveBtn = if (selectedLanguage == "EN") "Save to Fridge" else "Simpan ke Kulkas"

    // --- STATE ---
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    // Dropdown States
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = if (selectedLanguage == "EN") listOf("Food", "Drink", "Snack", "Fruit", "Veggie") else listOf("Makanan", "Minuman", "Camilan", "Buah", "Sayur")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    var unitExpanded by remember { mutableStateOf(false) }
    val units = listOf("Gram", "Kg", "Litre", "Pcs", "Bottle")
    var selectedUnit by remember { mutableStateOf(units[0]) }

    // DatePicker States
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(txtTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = { TextButton(onClick = onBackClick) { Text(txtCancel, color = Color.Red) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder Foto
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                Text(if (selectedLanguage == "EN") "Add Photo" else "Tambah Foto", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Nama Bahan
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(txtNameHint) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Dropdown Kategori
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory, onValueChange = {},
                    readOnly = true,
                    label = { Text(txtCategory) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    categories.forEach { selection ->
                        DropdownMenuItem(
                            text = { Text(selection) },
                            onClick = { selectedCategory = selection; categoryExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Baris Jumlah & Satuan
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = quantity, onValueChange = { quantity = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(txtQuantity) },
                    shape = RoundedCornerShape(12.dp)
                )

                // Dropdown Satuan
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = !unitExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedUnit, onValueChange = {},
                        readOnly = true,
                        label = { Text(txtUnit) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(expanded = unitExpanded, onDismissRequest = { unitExpanded = false }) {
                        units.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = { selectedUnit = selection; unitExpanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Pilih Tanggal Kadaluarsa
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (selectedDateMillis == null) Color.LightGray else PrimaryFigmaGreen)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = PrimaryFigmaGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (selectedDateMillis != null) dateFormatter.format(Date(selectedDateMillis!!)) else txtDateBtn,
                        color = if (selectedDateMillis != null) Color.Black else Color.Gray
                    )
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedDateMillis = datePickerState.selectedDateMillis
                            showDatePicker = false
                        }) { Text("OK") }
                    }
                ) { DatePicker(state = datePickerState) }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    onSaveClick(
                        InventoryItem(
                            name = name,
                            category = selectedCategory,
                            quantity = quantity.toDoubleOrNull() ?: 0.0,
                            unit = selectedUnit,
                            purchaseDate = System.currentTimeMillis(),
                            expiryDate = selectedDateMillis ?: (System.currentTimeMillis() + 604800000)
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryFigmaGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotEmpty() && quantity.isNotEmpty() && selectedDateMillis != null
            ) { Text(txtSaveBtn, fontWeight = FontWeight.Bold) }
        }
    }
}