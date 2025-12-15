package ir.wpstorm.shams.ui.screens.lessons

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.wpstorm.shams.data.db.DownloadedAudioEntity
import ir.wpstorm.shams.ui.components.AudioPlayerCompose
import ir.wpstorm.shams.ui.components.GlobalError
import ir.wpstorm.shams.ui.components.GlobalLoading
import ir.wpstorm.shams.ui.components.HighlightableHtmlRenderer
import ir.wpstorm.shams.ui.components.SearchNavigationBar
import ir.wpstorm.shams.ui.theme.Emerald400
import ir.wpstorm.shams.ui.theme.Emerald700
import ir.wpstorm.shams.ui.theme.Gray50
import ir.wpstorm.shams.ui.theme.Gray700
import ir.wpstorm.shams.ui.theme.Gray900
import ir.wpstorm.shams.util.DownloadHelper
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModel
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModelFactory
import ir.wpstorm.shams.viewmodel.LessonViewModel
import ir.wpstorm.shams.viewmodel.LessonViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun LessonScreen(
    lessonId: Int,
    searchQuery: String = "",
    globalAudioPlayerViewModel: GlobalAudioPlayerViewModel,
    @Suppress("UNUSED_PARAMETER") onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ir.wpstorm.shams.ShamsApplication
    val repository = application.lessonRepository
    val downloadedAudioRepository = application.downloadedAudioRepository
    val progressRepository = application.progressRepository
    val factory = LessonViewModelFactory(repository)
    val viewModel: LessonViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Use global Audio Player for app-wide audio management (same instance as mini player)
    val globalAudioPlayer = application.globalAudioPlayer
    var localAudioPath by remember { mutableStateOf<String?>(null) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var isDownloaded by remember { mutableStateOf(false) }

    // Search navigation state
    var totalMatches by remember { mutableStateOf(0) }
    var currentMatchIndex by remember { mutableStateOf(0) }

    // Completion tracking state
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
        // Check if file is already downloaded using database
        val downloadedAudio = downloadedAudioRepository.getDownloadedAudioByLessonId(lessonId)
        if (downloadedAudio != null) {
            val file = File(downloadedAudio.filePath)
            if (file.exists()) {
                localAudioPath = downloadedAudio.filePath
                isDownloaded = true
            } else {
                // File no longer exists, remove from database
                downloadedAudioRepository.deleteDownloadedAudio(downloadedAudio)
            }
        }
        // Check if lesson is completed
        isCompleted = progressRepository.isLessonCompleted(lessonId)
        // Update last accessed lesson
        uiState.lesson?.categoryId?.let { categoryId ->
            progressRepository.updateLastAccessedLesson(categoryId, lessonId)
        }
    }

    // Prepare audio when lesson is loaded
    LaunchedEffect(uiState.lesson?.audioUrl, localAudioPath) {
        uiState.lesson?.audioUrl?.let { url ->
            val path = localAudioPath ?: url
            globalAudioPlayer.prepare(path)

            // Set current audio in global state (for mini player)
            val currentAudio = if (isDownloaded && localAudioPath != null) {
                downloadedAudioRepository.getDownloadedAudioByLessonId(lessonId)
            } else {
                // Create a temporary entity for streaming audio
                uiState.lesson?.let { lesson ->
                    lesson.audioUrl?.let { audioUrl ->
                        DownloadedAudioEntity(
                            id = "streaming_$lessonId",
                            lessonId = lessonId,
                            title = lesson.title,
                            filePath = audioUrl,
                            fileSize = 0L,
                            downloadDate = System.currentTimeMillis()
                        )
                    }
                }
            }
            currentAudio?.let { audio ->
                globalAudioPlayerViewModel.setCurrentAudio(audio)
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (MaterialTheme.colorScheme.background == Color.White) {
                        Gray50
                    } else {
                        Gray900
                    }
                ),
            bottomBar = {
                Column {
                    // Show search navigation if there's a search query and matches
                    if (searchQuery.isNotBlank() && totalMatches > 0) {
                        SearchNavigationBar(
                            searchQuery = searchQuery,
                            currentMatchIndex = currentMatchIndex,
                            totalMatches = totalMatches,
                            onPreviousMatch = {
                                if (currentMatchIndex > 0) {
                                    currentMatchIndex--
                                }
                            },
                            onNextMatch = {
                                if (currentMatchIndex < totalMatches - 1) {
                                    currentMatchIndex++
                                }
                            }
                        )
                    }

                    // Audio Player at bottom if lesson has audio
                    if (uiState.lesson?.audioUrl != null) {
                        AudioPlayerCompose(
                            audioPlayer = globalAudioPlayer,
                            postTitle = uiState.lesson?.title ?: "",
                            onPlayStart = { _ ->
                                // When audio starts playing, update global player state for mini player display
                                scope.launch {
                                    // Small delay to ensure audio player state is updated
                                    kotlinx.coroutines.delay(50)
                                    val currentAudio = if (isDownloaded && localAudioPath != null) {
                                        downloadedAudioRepository.getDownloadedAudioByLessonId(lessonId)
                                    } else {
                                        // Create a temporary entity for streaming audio
                                        uiState.lesson?.let { lesson ->
                                            lesson.audioUrl?.let { url ->
                                                DownloadedAudioEntity(
                                                    id = "streaming_$lessonId",
                                                    lessonId = lessonId,
                                                    title = lesson.title,
                                                    filePath = url,
                                                    fileSize = 0L,
                                                    downloadDate = System.currentTimeMillis()
                                                )
                                            }
                                        }
                                    }
                                    currentAudio?.let { audio ->
                                        globalAudioPlayerViewModel.setCurrentAudio(audio)
                                    }
                                }
                            },
                            onDownload = {
                                scope.launch {
                                    uiState.lesson?.let { lesson ->
                                        lesson.audioUrl?.let { url ->
                                            isDownloading = true
                                            downloadProgress = 0f
                                            val fileName = "lesson_${lessonId}.mp3"
                                            val path = DownloadHelper.downloadFile(
                                                context = context,
                                                fileUrl = url,
                                                fileName = fileName,
                                                onProgress = { progress ->
                                                    downloadProgress = progress
                                                }
                                            )
                                            isDownloading = false
                                            if (path != null) {
                                                val file = File(path)
                                                val fileSize = if (file.exists()) file.length() else 0L

                                                // Save to database
                                                val downloadedAudio = DownloadedAudioEntity(
                                                    id = path,
                                                    lessonId = lessonId,
                                                    title = lesson.title,
                                                    filePath = path,
                                                    fileSize = fileSize,
                                                    downloadDate = System.currentTimeMillis()
                                                )
                                                downloadedAudioRepository.insertDownloadedAudio(downloadedAudio)

                                                localAudioPath = path
                                                isDownloaded = true
                                                globalAudioPlayer.prepare(path) // Prepare with local file
                                                Toast.makeText(context, "دانلود کامل شد!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "خطا در دانلود", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            },
                            onDelete = {
                                scope.launch {
                                    localAudioPath?.let { path ->
                                        try {
                                            val file = File(path)
                                            if (file.exists() && file.delete()) {
                                                // Remove from database
                                                downloadedAudioRepository.getDownloadedAudioByLessonId(lessonId)?.let { audio ->
                                                    downloadedAudioRepository.deleteDownloadedAudio(audio)
                                                }

                                                localAudioPath = null
                                                isDownloaded = false
                                                // Re-prepare audio with original URL
                                                uiState.lesson?.audioUrl?.let { url ->
                                                    globalAudioPlayer.prepare(url)
                                                }
                                                Toast.makeText(context, "فایل حذف شد", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "خطا در حذف فایل", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (_: Exception) {
                                            @Suppress("UNUSED_PARAMETER")
                                            Toast.makeText(context, "خطا در حذف فایل", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            isDownloading = isDownloading,
                            downloadProgress = downloadProgress,
                            isDownloaded = isDownloaded
                        )
                    }
                }
            }
        ) { paddingValues ->
            when {
                uiState.error != null -> {
                    GlobalError(
                        type = "network",
                        message = uiState.error,
                        onRetry = { viewModel.loadLesson(lessonId) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.isLoading -> {
                    GlobalLoading(
                        message = "در حال بارگذاری درس...",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.lesson != null -> {
                    val lesson = uiState.lesson!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Lesson header
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 24.dp)
                        ) {
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    lineHeight = 32.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "آیت الله سید محمدرضا حسینی آملی (حفظه الله)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray700,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Mark as complete button
                            if (isCompleted) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            lesson.categoryId?.let { categoryId ->
                                                progressRepository.unmarkLessonAsCompleted(lessonId, categoryId)
                                                isCompleted = false
                                                Toast.makeText(context, "علامت تکمیل حذف شد", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (MaterialTheme.colorScheme.surface == Color.White) {
                                            Emerald700
                                        } else {
                                            Emerald400
                                        }
                                    )
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Text("تکمیل شده")
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            lesson.categoryId?.let { categoryId ->
                                                progressRepository.markLessonAsCompleted(lessonId, categoryId)
                                                isCompleted = true
                                                Toast.makeText(context, "درس به عنوان تکمیل شده علامت‌گذاری شد", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Text("علامت‌گذاری به عنوان تکمیل شده")
                                    }
                                }
                            }
                        }

                        // Lesson content
                        if (lesson.content.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                Text(
                                    text = "محتوای درس",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 20.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                )

                                // HTML content renderer with highlighting
                                HighlightableHtmlRenderer(
                                    html = lesson.content,
                                    searchQuery = searchQuery,
                                    currentMatchIndex = currentMatchIndex,
                                    onMatchesFound = { matches ->
                                        totalMatches = matches
                                        if (matches > 0 && currentMatchIndex >= matches) {
                                            currentMatchIndex = 0
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "درس یافت نشد",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray700,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
