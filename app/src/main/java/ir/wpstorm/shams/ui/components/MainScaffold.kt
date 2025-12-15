package ir.wpstorm.shams.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    globalAudioPlayerViewModel: GlobalAudioPlayerViewModel,
    content: @Composable (PaddingValues) -> Unit
) {

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination?.route

    // Check if current screen is a Lesson screen (detailed view)
    val isLessonScreen = currentDestination?.startsWith("lesson/") == true

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

    // Collect global audio player state
    val audioPlayerUiState by globalAudioPlayerViewModel.uiState.collectAsState()

    // Show mini player when:
    // 1. There's an active audio (currentAudio is not null)
    // 2. NOT on Lesson screen (where full player is shown)
    val showMiniPlayer = audioPlayerUiState.currentAudio != null && !isLessonScreen

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

            // Mini Audio Player (shown below header when not on Lesson screen)
            if (showMiniPlayer) {
                MiniAudioPlayer(
                    uiState = audioPlayerUiState,
                    onPlayPauseClick = {
                        globalAudioPlayerViewModel.togglePlayPause()
                    },
                    onCloseClick = {
                        globalAudioPlayerViewModel.stopAndClear()
                    },
                    onPlayerClick = {
                        // Navigate to the lesson screen where full player is shown
                        audioPlayerUiState.currentAudio?.let { audio ->
                            navController.navigate("lesson/${audio.lessonId}")
                        }
                    }
                )
            }

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
    }
}
