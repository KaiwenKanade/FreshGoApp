package com.example.freshgoapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.freshgoapp.data.Local.AppDatabase
import com.example.freshgoapp.data.Local.User
import com.example.freshgoapp.ui.add.AddItemScreen
import com.example.freshgoapp.ui.add.AddViewModel
import com.example.freshgoapp.ui.home.HomeScreen
import com.example.freshgoapp.ui.home.HomeViewModel
import com.example.freshgoapp.ui.home.InventoryListScreen
import com.example.freshgoapp.ui.home.ItemDetailScreen
import com.example.freshgoapp.ui.recipe.RecipeScreen
import com.example.freshgoapp.ui.theme.FreshGoAppTheme
import com.example.freshgoapp.ui.login.LoginScreen
import com.example.freshgoapp.ui.login.RegisterScreen
import com.example.freshgoapp.ui.login.ResetPasswordScreen
import com.example.freshgoapp.ui.RecipeMenu.RecipeMenuScreen
import com.example.freshgoapp.ui.setting.SettingsScreen
import com.example.freshgoapp.ui.setting.LanguageScreen
import com.example.freshgoapp.ui.profile.ProfileScreen
import com.example.freshgoapp.ui.onboarding.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        val homeViewModel = HomeViewModel(db.inventoryDao())
        val addViewModel = AddViewModel(db.inventoryDao())
        val userDao = db.userDao()
        val inventoryDao = db.inventoryDao()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            FreshGoAppTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                var appLanguage by remember { mutableStateOf("EN") }

                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current

                // --- ANTI WHITE SCREEN (SPAM CLICK PROTECTOR) ---
                val safePopBackStack: () -> Unit = {
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                }

                DisposableEffect(lifecycleOwner) {
                    val prefs = context.getSharedPreferences("freshgo_session", Context.MODE_PRIVATE)

                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_STOP) {
                            prefs.edit().putLong("last_bg_time", System.currentTimeMillis()).apply()
                        } else if (event == Lifecycle.Event.ON_START) {
                            val lastBgTime = prefs.getLong("last_bg_time", 0L)
                            if (lastBgTime != 0L) {
                                val timeInactive = System.currentTimeMillis() - lastBgTime
                                val tenMinutesInMillis = 10 * 60 * 1000L
                                if (timeInactive >= tenMinutesInMillis) {
                                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                                }
                                prefs.edit().putLong("last_bg_time", 0L).apply()
                            }
                        }
                    }

                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                NavHost(navController = navController, startDestination = "onboarding") {

                    composable("onboarding") {
                        OnboardingScreen(onFinish = {
                            navController.navigate("login") { popUpTo("onboarding") { inclusive = true } }
                        })
                    }

                    composable("login") {
                        LoginScreen(
                            selectedLanguage = appLanguage,
                            onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                            onNavigateToRegister = { navController.navigate("register") },
                            onNavigateToForgot = { navController.navigate("reset_password") },
                            verifyLogin = { email, pin ->
                                val user = userDao.login(email, pin)
                                user != null
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                            onBackToLogin = { safePopBackStack() },
                            insertUser = { newUser -> userDao.insertUser(newUser) },
                            clearInventory = { inventoryDao.deleteAllItems() }
                        )
                    }

                    composable("reset_password") {
                        ResetPasswordScreen(
                            selectedLanguage = appLanguage,
                            onBackToLogin = { safePopBackStack() },
                            updatePassword = { email, newPin ->
                                userDao.updatePassword(email, newPin)
                            }
                        )
                    }

                    composable("home") {
                        val items by homeViewModel.inventoryItems.collectAsState(initial = emptyList())
                        HomeScreen(
                            items = items,
                            selectedLanguage = appLanguage,
                            onAddItemClick = { navController.navigate("add") { launchSingleTop = true } },
                            onSeeAllClick = { navController.navigate("list") { launchSingleTop = true } },
                            onItemClick = { itemId -> navController.navigate("detail/$itemId") { launchSingleTop = true } },
                            onOrdersClick = { navController.navigate("recipe_menu") { launchSingleTop = true } },
                            onSettingsClick = { navController.navigate("settings") { launchSingleTop = true } }
                        )
                    }

                    composable("add") {
                        AddItemScreen(
                            selectedLanguage = appLanguage,
                            itemToEdit = null,
                            onSaveClick = { newItem ->
                                addViewModel.saveItem(newItem)
                                safePopBackStack()
                            },
                            onBackClick = { safePopBackStack() }
                        )
                    }

                    composable("settings") {
                        var currentUser by remember { mutableStateOf<User?>(null) }
                        LaunchedEffect(Unit) { currentUser = userDao.getCurrentUser() }

                        SettingsScreen(
                            user = currentUser,
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = !isDarkTheme },
                            selectedLanguage = appLanguage,
                            onLanguageNavigate = { navController.navigate("language") { launchSingleTop = true } },
                            onProfileNavigate = { navController.navigate("profile") { launchSingleTop = true } },
                            onBackClick = { safePopBackStack() }
                        )
                    }

                    composable("profile") {
                        var currentUser by remember { mutableStateOf<User?>(null) }
                        LaunchedEffect(Unit) { currentUser = userDao.getCurrentUser() }

                        ProfileScreen(
                            user = currentUser,
                            selectedLanguage = appLanguage,
                            onBackClick = { safePopBackStack() },
                            onSignOutClick = { navController.navigate("login") { popUpTo(0) { inclusive = true } } }
                        )
                    }

                    composable("language") {
                        LanguageScreen(
                            currentLanguage = appLanguage,
                            onLanguageSelected = { appLanguage = it; safePopBackStack() },
                            onBackClick = { safePopBackStack() }
                        )
                    }

                    composable("recipe_menu") {
                        RecipeMenuScreen(
                            selectedLanguage = appLanguage,
                            onBackClick = { safePopBackStack() },
                            onSearchClick = { ingredient ->
                                navController.navigate("recipe/$ingredient") { launchSingleTop = true }
                            }
                        )
                    }

                    composable("list") {
                        val items by homeViewModel.inventoryItems.collectAsState(initial = emptyList())
                        InventoryListScreen(
                            items = items,
                            onBackClick = { safePopBackStack() },
                            onDeleteItems = { itemsToDelete -> itemsToDelete.forEach { item -> homeViewModel.deleteItem(item.id) } },
                            onEditItem = { id -> navController.navigate("edit/$id") { launchSingleTop = true } },
                            onItemClick = { itemId -> navController.navigate("detail/$itemId") { launchSingleTop = true } }
                        )
                    }

                    composable("detail/{itemId}") { backStackEntry ->
                        val itemIdStr = backStackEntry.arguments?.getString("itemId")
                        val itemId = itemIdStr?.toIntOrNull()

                        if (itemId != null) {
                            val item by homeViewModel.getInventoryItemById(itemId).collectAsState(initial = null)
                            item?.let {
                                ItemDetailScreen(
                                    item = it,
                                    selectedLanguage = appLanguage,
                                    onBackClick = { safePopBackStack() },
                                    onEditClick = { id -> navController.navigate("edit/$id") { launchSingleTop = true } },
                                    onDeleteClick = { id -> homeViewModel.deleteItem(id); safePopBackStack() }
                                )
                            }
                        }
                    }

                    composable("edit/{itemId}") { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
                        if (itemId != null) {
                            val itemToEdit by homeViewModel.getInventoryItemById(itemId).collectAsState(initial = null)

                            itemToEdit?.let { existingItem ->
                                AddItemScreen(
                                    selectedLanguage = appLanguage,
                                    itemToEdit = existingItem,
                                    onSaveClick = { updatedItem ->
                                        addViewModel.saveItem(updatedItem)
                                        safePopBackStack()
                                    },
                                    onBackClick = { safePopBackStack() }
                                )
                            }
                        }
                    }
                    
                    composable("recipe/{ingredientName}") { backStackEntry ->
                        val ingredientName = backStackEntry.arguments?.getString("ingredientName") ?: ""
                        RecipeScreen(
                            ingredientName = ingredientName,
                            selectedLanguage = appLanguage,
                            onBackClick = { safePopBackStack() },
                            onRecipeClick = { recipeId ->
                                navController.navigate("recipe_detail/$recipeId") { launchSingleTop = true }
                            }
                        )
                    }

                    composable("recipe_detail/{recipeId}") { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""

                        com.example.freshgoapp.ui.recipe.RecipeDetailScreen(
                            recipeId = recipeId,
                            selectedLanguage = appLanguage,
                            onBackClick = { safePopBackStack() }
                        )
                    }
                }
            }
        }
    }
}