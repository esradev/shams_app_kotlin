package ir.wpstorm.shams.ui.screens.lessons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.wpstorm.shams.viewmodel.LessonsListViewModel
import ir.wpstorm.shams.viewmodel.LessonsListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsListScreen(
    categoryId: Int,
    onLessonClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ir.wpstorm.shams.ShamsApplication
    val repository = application.lessonRepository
    val factory = LessonsListViewModelFactory(repository)
    val viewModel: LessonsListViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    // Validate categoryId
    if (categoryId <= 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Invalid category ID",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = onBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    LaunchedEffect(categoryId) {
        viewModel.loadLessonsForCategory(categoryId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Lessons") },
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = {
                            viewModel.loadLessonsForCategory(categoryId)
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }
            uiState.lessons.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.lessons) { lesson ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onLessonClick(lesson.id) }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = lesson.title.take(200), // Limit title length
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                if (lesson.content.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val cleanContent = lesson.content.replace(Regex("<[^>]*>"), "").trim()
                                    Text(
                                        text = cleanContent.take(100) + if (cleanContent.length > 100) "..." else "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No lessons found in this category")
                }
            }
        }
    }
}
