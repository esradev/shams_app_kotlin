package ir.wpstorm.shams.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import ir.wpstorm.shams.ui.components.EmptyState
import ir.wpstorm.shams.ui.components.GlobalError
import ir.wpstorm.shams.ui.components.GlobalLoading
import ir.wpstorm.shams.ui.theme.Emerald400
import ir.wpstorm.shams.ui.theme.Emerald50
import ir.wpstorm.shams.ui.theme.Emerald700
import ir.wpstorm.shams.ui.theme.Gray200
import ir.wpstorm.shams.ui.theme.Gray50
import ir.wpstorm.shams.ui.theme.Gray500
import ir.wpstorm.shams.ui.theme.Gray700
import ir.wpstorm.shams.ui.theme.Gray900
import ir.wpstorm.shams.viewmodel.CourseProgressWithCategory
import ir.wpstorm.shams.viewmodel.ProgressViewModel
import ir.wpstorm.shams.viewmodel.ProgressViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    onCategoryClick: (Int) -> Unit,
    onContinueLearning: (categoryId: Int, lessonId: Int) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ir.wpstorm.shams.ShamsApplication

    val progressRepository = application.progressRepository
    val categoryRepository = application.categoryRepository

    val progressFactory = ProgressViewModelFactory(progressRepository, categoryRepository)
    val progressViewModel: ProgressViewModel = viewModel(factory = progressFactory)
    val progressUiState by progressViewModel.uiState.collectAsState()

    val isDarkTheme = MaterialTheme.colorScheme.background == Gray900

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDarkTheme) Gray900 else Gray50
                )
        ) {
            when {
                progressUiState.error != null -> {
                    GlobalError(
                        type = "network",
                        message = progressUiState.error,
                        onRetry = { progressViewModel.retryLoading() }
                    )
                }

                progressUiState.isLoading -> {
                    GlobalLoading(
                        message = "در حال بارگذاری پیشرفت..."
                    )
                }

                progressUiState.courseProgress.isEmpty() -> {
                    EmptyState(
                        message = "شما هنوز هیچ درسی را شروع نکرده‌اید",
                        onRetry = null
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Text(
                                    text = "پیشرفت یادگیری",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Right
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "دوره‌هایی که در حال یادگیری آن‌ها هستید",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Gray700,
                                    textAlign = TextAlign.Right
                                )
                            }
                        }

                        items(progressUiState.courseProgress) { progressItem ->
                            ProgressCard(
                                progressItem = progressItem,
                                onClick = { onCategoryClick(progressItem.progress.categoryId) },
                                onContinueLearning = {
                                    // Use first uncompleted lesson if available, otherwise use last accessed lesson
                                    val lessonId = progressItem.nextUncompletedLessonId
                                        ?: progressItem.progress.lastLessonId
                                    onContinueLearning(
                                        progressItem.progress.categoryId,
                                        lessonId
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(
    progressItem: CourseProgressWithCategory,
    onClick: () -> Unit,
    onContinueLearning: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Category name
            Text(
                text = progressItem.categoryName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${progressItem.progressPercentage}%",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (MaterialTheme.colorScheme.surface == Color.White) {
                        Emerald700
                    } else {
                        Emerald400
                    }
                )

                Text(
                    text = "${progressItem.progress.completedLessons} از ${progressItem.progress.totalLessons} درس",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray700,
                    textAlign = TextAlign.Right
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = progressItem.progressPercentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (MaterialTheme.colorScheme.surface == Color.White) {
                    Emerald700
                } else {
                    Emerald400
                },
                trackColor = Gray200
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Last accessed info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Continue button
                Row(
                    modifier = Modifier
                        .clickable { onContinueLearning() }
                        .background(
                            color = if (MaterialTheme.colorScheme.surface == Color.White) {
                                Emerald50
                            } else {
                                Emerald700.copy(alpha = 0.3f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "ادامه",
                        modifier = Modifier.size(18.dp),
                        tint = if (MaterialTheme.colorScheme.surface == Color.White) {
                            Emerald700
                        } else {
                            Emerald400
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ادامه یادگیری",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (MaterialTheme.colorScheme.surface == Color.White) {
                            Emerald700
                        } else {
                            Emerald400
                        }
                    )
                }

                Text(
                    text = formatDate(progressItem.progress.lastAccessedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        minutes < 1 -> "الان"
        minutes < 60 -> "$minutes دقیقه پیش"
        hours < 24 -> "$hours ساعت پیش"
        days < 7 -> "$days روز پیش"
        else -> {
            val formatter = SimpleDateFormat("yyyy/MM/dd", Locale("fa", "IR"))
            formatter.format(Date(timestamp))
        }
    }
}
