package ir.wpstorm.shams.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ir.wpstorm.shams.ui.theme.ThemeManager
import ir.wpstorm.shams.ui.theme.rememberThemeState

@Composable
fun MainScaffold(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val themeManager = remember { ThemeManager(context) }
    val themeState = rememberThemeState(themeManager)

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination?.route

    val isHome = currentDestination == "categories"
    val showBackButton = !isHome && currentDestination != "search" && currentDestination != "settings"

    // Get page title based on current destination
    val pageTitle = when {
        currentDestination == "search" -> "جستجو"
        currentDestination == "settings" -> "تنظیمات"
        currentDestination?.startsWith("lessons/") == true -> "دروس"
        currentDestination?.startsWith("lesson/") == true -> "درس"
        else -> ""
    }

    // Show tab bar only on main screens
    val showTabBar = currentDestination in listOf("categories", "search", "settings")

    Scaffold(
        topBar = {
            AppHeader(
                title = pageTitle,
                showBackButton = showBackButton,
                isHome = isHome,
                onBackClick = {
                    navController.popBackStack()
                },
                onThemeToggle = themeState.toggleTheme,
                isDarkTheme = themeState.isDarkTheme
            )
        },
        bottomBar = {
            if (showTabBar) {
                CustomTabBar(
                    navController = navController,
                    isDarkTheme = themeState.isDarkTheme
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        content(paddingValues)
    }
}
