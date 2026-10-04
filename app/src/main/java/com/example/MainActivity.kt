package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.LifeHubNavGraph
import com.example.ui.theme.LifeHubTheme

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : NavItem("home", "Home", Icons.Default.Home)
    object Categories : NavItem("categories", "Categories", Icons.Default.Category)
    object AI : NavItem("ai_assistant", "AI", Icons.Default.AutoAwesome)
    object Favorites : NavItem("favorites", "Favorites", Icons.Default.Favorite)
    object Settings : NavItem("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeMode by remember { mutableStateOf("System") }
            var language by remember { mutableStateOf("en") }

            val isDark = when (themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }

            LifeHubTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavItems = listOf(
                    NavItem.Home,
                    NavItem.Categories,
                    NavItem.AI,
                    NavItem.Favorites,
                    NavItem.Settings
                )

                val showBottomBar = currentRoute in listOf(
                    NavItem.Home.route,
                    NavItem.Categories.route,
                    NavItem.AI.route,
                    NavItem.Favorites.route,
                    NavItem.Settings.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                modifier = Modifier.testTag("main_bottom_nav"),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 3.dp
                            ) {
                                bottomNavItems.forEach { item ->
                                    val selected = currentRoute == item.route
                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.title) },
                                        label = { Text(item.title) },
                                        selected = selected,
                                        modifier = Modifier.testTag("bottom_nav_${item.route}"),
                                        onClick = {
                                            if (currentRoute != item.route) {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    LifeHubNavGraph(
                        navController = navController,
                        currentTheme = themeMode,
                        onThemeChange = { themeMode = it },
                        currentLanguage = language,
                        onLanguageChange = { language = it },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
