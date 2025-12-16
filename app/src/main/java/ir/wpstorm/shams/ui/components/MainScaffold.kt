package ir.wpstorm.shams.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ir.wpstorm.shams.ShamsApplication
import ir.wpstorm.shams.ui.theme.ThemeState
import ir.wpstorm.shams.ui.theme.TextSizeState
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun MainScaffold(
    navController: NavController,
    themeState: ThemeState,
    textSizeState: TextSizeState,
    globalAudioPlayerViewModel: GlobalAudioPlayerViewModel,
    onNavigateToLesson: (Int) -> Unit = {},
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

    // Show tab bar on all screens except lesson detail screen
    val showTabBar = !isLessonScreen

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
                isDarkTheme = themeState.isDarkTheme,
                onTextSizeClick = textSizeState.increaseTextSize
            )

            // Mini Audio Player (shown below header when not on Lesson screen)
            if (showMiniPlayer) {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                val application = context.applicationContext as ShamsApplication
                val downloadedAudioRepository = application.downloadedAudioRepository

                var isDownloaded by remember(audioPlayerUiState.currentAudio?.id) { mutableStateOf(false) }
                var isDownloading by remember(audioPlayerUiState.currentAudio?.id) { mutableStateOf(false) }
                var downloadProgress by remember(audioPlayerUiState.currentAudio?.id) { mutableStateOf(0f) }

                // Check download status
                LaunchedEffect(audioPlayerUiState.currentAudio?.lessonId) {
                    audioPlayerUiState.currentAudio?.lessonId?.let { lessonId ->
                        val downloadedAudio = downloadedAudioRepository.getDownloadedAudioByLessonId(lessonId)
                        isDownloaded = downloadedAudio != null && File(downloadedAudio.filePath).exists()
                    }
                }

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
                    },
                    onSeek = { position ->
                        globalAudioPlayerViewModel.seekTo(position)
                    },
                    onDownload = {
                        scope.launch {
                            audioPlayerUiState.currentAudio?.let { audio ->
                                // Navigate to lesson to download
                                navController.navigate("lesson/${audio.lessonId}")
                            }
                        }
                    },
                    onDelete = {
                        scope.launch {
                            audioPlayerUiState.currentAudio?.lessonId?.let { lessonId ->
                                try {
                                    val downloadedAudio = downloadedAudioRepository.getDownloadedAudioByLessonId(lessonId)
                                    downloadedAudio?.let { audio ->
                                        val file = File(audio.filePath)
                                        if (file.exists() && file.delete()) {
                                            downloadedAudioRepository.deleteDownloadedAudio(audio)
                                            isDownloaded = false
                                            Toast.makeText(context, "فایل حذف شد", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "خطا در حذف فایل", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    onSpeedChange = {
                        // Cycle through speeds: 1.0x -> 1.25x -> 1.5x -> 1.75x -> 2.0x -> 1.0x
                        val currentSpeed = audioPlayerUiState.playbackSpeed
                        val newSpeed = when {
                            currentSpeed < 1.25f -> 1.25f
                            currentSpeed < 1.5f -> 1.5f
                            currentSpeed < 1.75f -> 1.75f
                            currentSpeed < 2.0f -> 2.0f
                            else -> 1.0f
                        }
                        globalAudioPlayerViewModel.setPlaybackSpeed(newSpeed)
                    },
                    isDownloaded = isDownloaded,
                    isDownloading = isDownloading
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
