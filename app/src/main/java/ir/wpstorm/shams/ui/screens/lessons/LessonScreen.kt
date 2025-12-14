package ir.wpstorm.shams.ui.screens.lessons

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.wpstorm.shams.data.db.AppDatabase
import ir.wpstorm.shams.data.repository.LessonRepository
import ir.wpstorm.shams.player.AudioPlayer
import ir.wpstorm.shams.util.DownloadHelper
import ir.wpstorm.shams.util.HtmlRenderer
import ir.wpstorm.shams.viewmodel.LessonViewModel
import ir.wpstorm.shams.viewmodel.LessonViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ir.wpstorm.shams.ShamsApplication
    val repository = application.lessonRepository
    val factory = LessonViewModelFactory(repository)
    val viewModel: LessonViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Audio Player
    val audioPlayer = remember { AudioPlayer(context) }
    var localAudioPath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("Lesson Detail") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            uiState.lesson != null -> {
                val lesson = uiState.lesson!!

                // 🔹 Render HTML content
                HtmlRenderer(html = lesson.content)

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // 🔹 Play/Pause Button
                    Button(onClick = {
                        if (audioPlayer.isPlaying.value == true) {
                            audioPlayer.pause()
                        } else {
                            val path = localAudioPath ?: lesson.audioUrl
                            if (path != null) audioPlayer.prepare(path)
                            audioPlayer.play()
                        }
                    }) {
                        Text(if (audioPlayer.isPlaying.value == true) "Pause" else "Play")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 🔹 Download Button
                    lesson.audioUrl?.let { url ->
                        Button(onClick = {
                            scope.launch {
                                val fileName = "lesson_${lesson.id}.mp3"
                                val path = DownloadHelper.downloadFile(context, url, fileName)
                                if (path != null) {
                                    localAudioPath = path
                                    Toast.makeText(context, "Downloaded!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Text("Download")
                        }
                    }
                }
            }
        }
    }
}
