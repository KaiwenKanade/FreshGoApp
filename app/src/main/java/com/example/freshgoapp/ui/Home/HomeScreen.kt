package com.example.freshgoapp.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.freshgoapp.data.Local.InventoryItem
import com.example.freshgoapp.ui.theme.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    items: List<InventoryItem>,
    onAddItemClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onOrdersClick: () -> Unit,
    selectedLanguage: String,
    onSettingsClick: () -> Unit
) {
    val filterLabels = if (selectedLanguage == "EN") {
        listOf("All", "Food", "Drink", "Expired", "Fresh")
    } else {
        listOf("Semua", "Makanan", "Minuman", "Kadaluarsa", "Segar")
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(filterLabels[0]) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    // --- OPTIMASI 1: Cegah kalkulasi ulang yang tidak perlu (Hemat CPU/RAM) ---
    val expiringItems by remember(items) {
        derivedStateOf {
            items.filter { item ->
                val daysLeft = TimeUnit.MILLISECONDS.toDays(item.expiryDate - System.currentTimeMillis()).toInt()
                daysLeft <= 3
            }
        }
    }

    // --- OPTIMASI 2: Kalkulasi filter yang lebih cerdas ---
    val filteredItems by remember(items, searchQuery, selectedFilter) {
        derivedStateOf {
            items.filter { item ->
                item.name.contains(searchQuery, ignoreCase = true) ||
                        item.category.contains(searchQuery, ignoreCase = true)
            }.let { list ->
                when (selectedFilter) {
                    "Makanan", "Food" -> list.filter { it.category.equals("Makanan", ignoreCase = true) || it.category.equals("Food", ignoreCase = true) }
                    "Minuman", "Drink" -> list.filter { it.category.equals("Minuman", ignoreCase = true) || it.category.equals("Drink", ignoreCase = true) }
                    "Expired", "Kadaluarsa" -> list.sortedBy { it.expiryDate }
                    "Fresh", "Segar" -> list.sortedByDescending { it.expiryDate }
                    else -> list
                }
            }
        }
    }

    val textGreeting = if (selectedLanguage == "EN") "Good morning," else "Selamat pagi,"
    val textSearch = if (selectedLanguage == "EN") "Search your pantry..." else "Cari bahan dapur..."
    val textTotal = if (selectedLanguage == "EN") "Total Items" else "Total Bahan"
    val textWasted = if (selectedLanguage == "EN") "Wasted (Expired)" else "Terbuang (Basi)"
    val textInventory = if (selectedLanguage == "EN") "My Inventory" else "Inventaris Saya"
    val textSeeAll = if (selectedLanguage == "EN") "See all ->" else "Lihat semua ->"
    val textEmpty = if (selectedLanguage == "EN") "No items found." else "Bahan tidak ditemukan."

    val notifTitle = if (selectedLanguage == "EN") "Pantry Alerts" else "Pemberitahuan Dapur"
    val notifEmpty = if (selectedLanguage == "EN") "All ingredients inside your fridge are fresh and safe!" else "Semua bahan di dalam kulkas aman dan masih segar!"
    val notifDesc = if (selectedLanguage == "EN") "The following items are expired or expiring within 3 days:" else "Bahan-bahan berikut sudah basi atau akan basi dalam kurung waktu 3 hari:"

    // Optimasi jumlah yang statis
    val totalBahan = items.size
    val sudahKadaluarsa = remember(items) { items.count { TimeUnit.MILLISECONDS.toDays(it.expiryDate - System.currentTimeMillis()) < 0 } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddItemClick,
                containerColor = PrimaryFigmaGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, contentDescription = "Add") }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text(if (selectedLanguage == "EN") "Home" else "Beranda") })
                NavigationBarItem(selected = false, onClick = onAddItemClick, icon = { Icon(Icons.Default.Add, null) }, label = { Text(if (selectedLanguage == "EN") "Add" else "Tambah") })
                NavigationBarItem(selected = false, onClick = onOrdersClick, icon = { Icon(Icons.Default.RestaurantMenu, null) }, label = { Text(if (selectedLanguage == "EN") "Recipes" else "Resep") })
                NavigationBarItem(selected = false, onClick = onSettingsClick, icon = { Icon(Icons.Default.Settings, null) }, label = { Text(if (selectedLanguage == "EN") "Settings" else "Setelan") })
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)

                    IconButton(onClick = { showNotificationDialog = true }) {
                        BadgedBox(
                            badge = {
                                if (expiringItems.isNotEmpty()) {
                                    Badge(containerColor = CriticalRed) {
                                        Text(text = expiringItems.size.toString(), color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (expiringItems.isNotEmpty()) CriticalRed else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) { append("$textGreeting\n") }
                        withStyle(style = SpanStyle(color = PrimaryFigmaGreen, fontWeight = FontWeight.Bold)) { append("Fresh Keeper.") }
                    },
                    fontSize = 28.sp, lineHeight = 34.sp
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(textSearch, color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = PrimaryFigmaGreen,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(filterLabels) { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            modifier = Modifier.clickable { selectedFilter = filter },
                            color = if (isSelected) PrimaryFigmaGreen else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard(title = textTotal, value = "$totalBahan Items", color = PrimaryFigmaGreen, modifier = Modifier.weight(1f))
                    SummaryCard(title = textWasted, value = "$sudahKadaluarsa Items", color = CriticalRed, modifier = Modifier.weight(1f))
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(textInventory, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
                    Text(text = textSeeAll, color = PrimaryFigmaGreen, fontSize = 12.sp, modifier = Modifier.clickable { onSeeAllClick() })
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (filteredItems.isEmpty()) {
                item { Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text(textEmpty, color = Color.Gray) } }
            } else {
                items(filteredItems) { item ->
                    InventoryCard(item = item, onClick = { onItemClick(item.id) }, lang = selectedLanguage)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (showNotificationDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationDialog = false },
                properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true),
                title = { Text(notifTitle, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (expiringItems.isEmpty()) {
                            Text(notifEmpty, color = Color.Gray)
                        } else {
                            Text(notifDesc, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            expiringItems.forEach { item ->
                                val daysLeft = TimeUnit.MILLISECONDS.toDays(item.expiryDate - System.currentTimeMillis()).toInt()
                                val alertText = when {
                                    daysLeft < 0 -> if(selectedLanguage == "EN") "• ${item.name} (Expired!)" else "• ${item.name} (Sudah Basi!)"
                                    daysLeft == 0 -> if(selectedLanguage == "EN") "• ${item.name} (Expires Today!)" else "• ${item.name} (Basi Hari Ini!)"
                                    else -> if(selectedLanguage == "EN") "• ${item.name} ($daysLeft days left)" else "• ${item.name} ($daysLeft hari lagi)"
                                }
                                Text(alertText, color = CriticalRed, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showNotificationDialog = false }) {
                        Text(if (selectedLanguage == "EN") "Close" else "Tutup", color = PrimaryFigmaGreen, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun InventoryCard(item: InventoryItem, onClick: () -> Unit, lang: String) {
    val daysLeft = TimeUnit.MILLISECONDS.toDays(item.expiryDate - System.currentTimeMillis()).toInt()
    val statusColor = when {
        daysLeft > 10 -> PrimaryGreen; daysLeft in 3..10 -> WarningAmber; daysLeft == 2 -> CriticalRed; daysLeft in 0..1 -> Color(0xFFB71C1C); else -> Color.Black
    }

    val statusText = when {
        daysLeft < 0 -> if(lang == "EN") "!!! EXPIRED" else "!!! BASI"
        daysLeft in 0..1 -> if(lang == "EN") "! $daysLeft DAYS LEFT" else "! $daysLeft HARI LAGI"
        else -> if(lang == "EN") "$daysLeft DAYS LEFT" else "$daysLeft HARI LAGI"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) { Text("🍎") }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("${item.quantity} ${item.unit} • ${item.category}", color = Color.Gray, fontSize = 11.sp)
            }
            Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(text = statusText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)), elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
        }
    }
}