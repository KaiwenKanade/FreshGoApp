package com.example.freshgoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.freshgoapp.data.AppDatabase
import com.example.freshgoapp.ui.add.AddItemScreen
import com.example.freshgoapp.ui.add.AddViewModel
import com.example.freshgoapp.ui.home.HomeScreen
import com.example.freshgoapp.ui.home.HomeViewModel
import com.example.freshgoapp.ui.home.InventoryListScreen
import com.example.freshgoapp.ui.home.ItemDetailScreen
import com.example.freshgoapp.ui.theme.FreshGoAppTheme
import com.example.freshgoapp.ui.login.LoginScreen
import com.example.freshgoapp.ui.login.RegisterScreen
import com.example.freshgoapp.ui.order.OrdersScreen
import com.example.freshgoapp.ui.setting.SettingsScreen
import com.example.freshgoapp.ui.setting.LanguageScreen
import com.example.freshgoapp.ui.profile.ProfileScreen
import com.example.freshgoapp.ui.onboarding.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi semua Database DAO di sini
        val db = AppDatabase.getDatabase(applicationContext)
        val homeViewModel = HomeViewModel(db.inventoryDao())
        val addViewModel = AddViewModel(db.inventoryDao())
        val userDao = db.userDao()
        val inventoryDao = db.inventoryDao()

        setContent {
            // SAKLAR UTAMA DARK MODE
            var isDarkTheme by remember { mutableStateOf(false) }

            // Tema aplikasi sekarang membaca status isDarkTheme
            FreshGoAppTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                var appLanguage by remember { mutableStateOf("EN") }

                NavHost(navController = navController, startDestination = "onboarding") {

                    // 1. ONBOARDING
                    composable("onboarding") {
                        OnboardingScreen(onFinish = {
                            navController.navigate("login") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        })
                    }

                    // 2. LOGIN
                    composable("login") {
                        LoginScreen(
                            selectedLanguage = appLanguage,
                            onLoginSuccess = {
                                navController.navigate("home") { popUpTo("login") { inclusive = true } }
                            },
                            onNavigateToRegister = { navController.navigate("register") },
                            verifyLogin = { email, pin ->
                                val user = userDao.login(email, pin)
                                user != null
                            }
                        )
                    }

                    // 3. REGISTER
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate("home") { popUpTo("login") { inclusive = true } }
                            },
                            onBackToLogin = { navController.popBackStack() },
                            insertUser = { newUser -> userDao.insertUser(newUser) },
                            clearInventory = { inventoryDao.deleteAllItems() }
                        )
                    }

                    // 4. HOME
                    composable("home") {
                        val items by homeViewModel.inventoryItems.collectAsState(initial = emptyList())
                        HomeScreen(
                            items = items,
                            selectedLanguage = appLanguage,
                            onAddItemClick = { navController.navigate("add") },
                            onSeeAllClick = { navController.navigate("list") },
                            onItemClick = { itemId -> navController.navigate("detail/$itemId") },
                            onOrdersClick = { navController.navigate("orders") },
                            onSettingsClick = { navController.navigate("settings") }
                        )
                    }

                    // 5. TAMBAH BAHAN (ADD)
                    composable("add") {
                        AddItemScreen(
                            selectedLanguage = appLanguage,
                            onSaveClick = { newItem ->
                                addViewModel.saveItem(newItem)
                                navController.popBackStack()
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    // 6. SETTINGS (SUDAH DIPERBARUI DENGAN DARK MODE & USER DATA)
                    composable("settings") {
                        var currentUser by remember { mutableStateOf<com.example.freshgoapp.data.User?>(null) }

                        LaunchedEffect(Unit) {
                            currentUser = userDao.getCurrentUser()
                        }

                        SettingsScreen(
                            user = currentUser,
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = !isDarkTheme },
                            selectedLanguage = appLanguage,
                            onLanguageNavigate = { navController.navigate("language") },
                            onProfileNavigate = { navController.navigate("profile") },
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    // 7. PROFILE
                    composable("profile") {
                        var currentUser by remember { mutableStateOf<com.example.freshgoapp.data.User?>(null) }

                        LaunchedEffect(Unit) {
                            currentUser = userDao.getCurrentUser()
                        }

                        ProfileScreen(
                            user = currentUser,
                            selectedLanguage = appLanguage,
                            onBackClick = { navController.popBackStack() },
                            onSignOutClick = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 8. LANGUAGE
                    composable("language") {
                        LanguageScreen(
                            currentLanguage = appLanguage,
                            onLanguageSelected = {
                                appLanguage = it
                                navController.popBackStack()
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    // 9. ORDERS
                    composable("orders") {
                        OrdersScreen(onBackClick = { navController.popBackStack() })
                    }

                    // 10. LIST
                    composable("list") {
                        val items by homeViewModel.inventoryItems.collectAsState(initial = emptyList())
                        InventoryListScreen(
                            items = items,
                            onBackClick = { navController.popBackStack() },
                            onDeleteItems = { itemsToDelete ->
                                itemsToDelete.forEach { item ->
                                    homeViewModel.deleteItem(item.id)
                                }
                            },
                            onEditItem = { },
                            onItemClick = { itemId -> navController.navigate("detail/$itemId") }
                        )
                    }

                    // 11. DETAIL
                    composable("detail/{itemId}") { backStackEntry ->
                        val itemIdStr = backStackEntry.arguments?.getString("itemId")
                        val itemId = itemIdStr?.toIntOrNull()

                        if (itemId != null) {
                            val item by homeViewModel.getInventoryItemById(itemId).collectAsState(initial = null)
                            item?.let {
                                ItemDetailScreen(
                                    item = it,
                                    selectedLanguage = appLanguage,
                                    onBackClick = { navController.popBackStack() },
                                    onEditClick = { },
                                    onDeleteClick = { id ->
                                        homeViewModel.deleteItem(id)
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}