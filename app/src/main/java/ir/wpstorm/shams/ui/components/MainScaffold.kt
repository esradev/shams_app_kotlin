package ir.wpstorm.shams.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ir.wpstorm.shams.ui.theme.ThemeState

@Composable
fun MainScaffold(
    navController: NavController,
    themeState: ThemeState,
    content: @Composable (PaddingValues) -> Unit
) {

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
                title = if (isHome) "شمس المعارف" else pageTitle,
                showBackButton = showBackButton,
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
