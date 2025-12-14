package ir.wpstorm.shams.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ir.wpstorm.shams.ShamsApplication
import ir.wpstorm.shams.ui.theme.ThemeState
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModel
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModelFactory

@Composable
fun MainScaffold(
    navController: NavController,
    themeState: ThemeState,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ShamsApplication

    val globalAudioPlayerViewModel: GlobalAudioPlayerViewModel = viewModel(
        factory = GlobalAudioPlayerViewModelFactory(application.globalAudioPlayer)
    )

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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            AppHeader(
                title = if (isHome) "شمس المعارف" else pageTitle,
                showBackButton = showBackButton,
                onBackClick = {
                    navController.popBackStack()
                },
                onThemeToggle = themeState.toggleTheme,
                isDarkTheme = themeState.isDarkTheme
            )

            // Main content
            Box(modifier = Modifier.weight(1f)) {
                val contentPaddingValues = PaddingValues(
                    bottom = 0.dp // Remove bottom padding to eliminate white space
                )
                content(contentPaddingValues)
            }

            // Tab Bar
            if (showTabBar) {
                CustomTabBar(
                    navController = navController,
                    isDarkTheme = themeState.isDarkTheme
                )
            }
        }

        // Mini Audio Player positioned absolutely at top after header
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp) // Account for header height
                .zIndex(10f)
        ) {
            MiniAudioPlayer(
                viewModel = globalAudioPlayerViewModel,
                onNavigateToLesson = { lessonId ->
                    navController.navigate("lesson/$lessonId")
                }
            )
        }

        // Full-screen Global Audio Player overlay (for expanded view)
        GlobalAudioPlayerCompose(
            viewModel = globalAudioPlayerViewModel,
            onNavigateToLesson = { lessonId ->
                navController.navigate("lesson/$lessonId")
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
