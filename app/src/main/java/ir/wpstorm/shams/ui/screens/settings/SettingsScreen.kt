package ir.wpstorm.shams.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.wpstorm.shams.ShamsApplication
import ir.wpstorm.shams.data.db.DownloadedAudioEntity
import ir.wpstorm.shams.ui.theme.Emerald700
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModel
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModelFactory
import ir.wpstorm.shams.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    onNavigateToLesson: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val application = context.applicationContext as ShamsApplication

    val globalAudioPlayerViewModel: GlobalAudioPlayerViewModel = viewModel(
        factory = GlobalAudioPlayerViewModelFactory(application.globalAudioPlayer)
    )
    val globalPlayerState by globalAudioPlayerViewModel.uiState

    // State for confirmation dialogs
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var audioToDelete by remember { mutableStateOf<DownloadedAudioEntity?>(null) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Add bottom padding for tab bar
        ) {
            item {
                Text(
                    text = "تنظیمات",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Settings Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        SettingSwitch(
                            title = "حالت آفلاین",
                            description = "فقط از دروس دانلود شده استفاده کن",
                            checked = uiState.offlineMode,
                            onCheckedChange = viewModel::setOfflineMode
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingSwitch(
                            title = "دانلود خودکار صوت",
                            description = "دانلود خودکار صوت درس‌ها",
                            checked = uiState.autoDownload,
                            onCheckedChange = viewModel::setAutoDownload
                        )
                    }
                }
            }

            // Downloaded Audio Files Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = Emerald700,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "فایل‌های صوتی دانلود شده",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "تعداد: ${uiState.downloadedAudios.size} فایل",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val totalSize = uiState.downloadedAudios.sumOf { it.fileSize }
                        Text(
                            text = "حجم کل: ${formatFileSize(totalSize)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (uiState.downloadedAudios.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { showDeleteAllDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("حذف همه فایل‌ها")
                            }
                        }
                    }
                }
            }

            // Downloaded Audio Files List
            if (uiState.downloadedAudios.isNotEmpty()) {
                items(uiState.downloadedAudios) { audio ->
                    DownloadedAudioItem(
                        audio = audio,
                        onDelete = { audioToDelete = audio },
                        onPlay = {
                            if (globalPlayerState.currentAudio?.lessonId == audio.lessonId && globalPlayerState.isPlaying) {
                                globalAudioPlayerViewModel.togglePlayPause()
                            } else {
                                globalAudioPlayerViewModel.playAudio(audio)
                                viewModel.updatePlayInfo(audio.lessonId)
                            }
                        },
                        onNavigateToLesson = { onNavigateToLesson(audio.lessonId) },
                        isCurrentlyPlaying = globalPlayerState.currentAudio?.lessonId == audio.lessonId && globalPlayerState.isPlaying
                    )
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        ) {
                            Text(
                                text = "هنوز فایل صوتی دانلود نشده است",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Error handling
            uiState.error?.let { errorMessage ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = viewModel::clearError) {
                                Text("باشه")
                            }
                        }
                    }
                }
            }

            // About Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "درباره برنامه",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "شمس المعارف - دروس خارج آیت الله حسینی آملی (حفظه الله)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "نسخه ۱.۰.۰",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = {
                Text(
                    text = "حذف همه فایل‌ها",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Right
                )
            },
            text = {
                Text(
                    text = "آیا مطمئن هستید که می‌خواهید همه فایل‌های دانلود شده را حذف کنید؟ این عمل قابل بازگشت نیست.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Right
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllDownloads()
                        showDeleteAllDialog = false
                    }
                ) {
                    Text("حذف همه")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Single Audio Delete Confirmation Dialog
    audioToDelete?.let { audio ->
        AlertDialog(
            onDismissRequest = { audioToDelete = null },
            title = {
                Text(
                    text = "حذف فایل",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Right
                )
            },
            text = {
                Text(
                    text = "آیا مطمئن هستید که می‌خواهید \"${audio.title}\" را حذف کنید؟",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Right
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAudio(audio)
                        audioToDelete = null
                    }
                ) {
                    Text("حذف فایل")
                }
            },
            dismissButton = {
                TextButton(onClick = { audioToDelete = null }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
private fun DownloadedAudioItem(
    audio: DownloadedAudioEntity,
    onDelete: () -> Unit,
    onPlay: () -> Unit,
    onNavigateToLesson: () -> Unit,
    isCurrentlyPlaying: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = audio.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "حجم: ${formatFileSize(audio.fileSize)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "تاریخ دانلود: ${formatDate(audio.downloadDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (audio.lastPlayed != null) {
                        Text(
                            text = "آخرین پخش: ${formatDate(audio.lastPlayed)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (audio.playCount > 0) {
                        Text(
                            text = "تعداد پخش: ${audio.playCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onPlay,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isCurrentlyPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isCurrentlyPlaying) "توقف" else "پخش")
                }

                OutlinedButton(
                    onClick = onNavigateToLesson,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("درس")
                }

                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حذف")
                }
            }
        }
    }
}

@Composable
fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes >= 1_000_000 -> String.format("%.1f مگابایت", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format("%.1f کیلوبایت", bytes / 1_000.0)
        else -> "$bytes بایت"
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale("fa", "IR"))
    return formatter.format(Date(timestamp))
}

