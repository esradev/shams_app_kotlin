package ir.wpstorm.shams.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay30
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.wpstorm.shams.util.formatTime
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModel

@Composable
fun GlobalAudioPlayerCompose(
    viewModel: GlobalAudioPlayerViewModel,
    onNavigateToLesson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Show full player only if there's current audio and it's not minimized
    if (uiState.currentAudio != null && !uiState.isMinimized) {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            FullPlayerView(
                uiState = uiState,
                onTogglePlayPause = viewModel::togglePlayPause,
                onSeekTo = viewModel::seekTo,
                onForward = viewModel::forward,
                onRewind = viewModel::rewind,
                onSetPlaybackSpeed = viewModel::setPlaybackSpeed,
                onMinimize = viewModel::minimize,
                onClose = viewModel::stopAndClear,
                onNavigateToLesson = onNavigateToLesson,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun FullPlayerView(
    uiState: ir.wpstorm.shams.viewmodel.GlobalAudioPlayerUiState,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onForward: () -> Unit,
    onRewind: () -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    onNavigateToLesson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with title and controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.currentAudio?.title ?: "",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Right
                )

                Row {
                    IconButton(onClick = onMinimize) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize"
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            if (uiState.isLoaded && uiState.duration > 0) {
                Column {
                    Slider(
                        value = uiState.currentPosition.toFloat(),
                        onValueChange = { onSeekTo(it.toLong()) },
                        valueRange = 0f..uiState.duration.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(uiState.currentPosition),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatTime(uiState.duration),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rewind
                IconButton(onClick = { onRewind() }) {
                    Icon(
                        imageVector = Icons.Default.Replay30,
                        contentDescription = "Rewind 30s"
                    )
                }

                // Play/Pause
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Forward
                IconButton(onClick = { onForward() }) {
                    Icon(
                        imageVector = Icons.Default.Forward30,
                        contentDescription = "Forward 30s"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Additional controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed control
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "سرعت:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row {
                        listOf(0.5f, 1f, 1.5f, 2f).forEach { speed ->
                            FilterChip(
                                onClick = { onSetPlaybackSpeed(speed) },
                                label = {
                                    Text(
                                        text = "${speed}x",
                                        fontSize = 12.sp
                                    )
                                },
                                selected = uiState.playbackSpeed == speed,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }

                // Navigate to lesson
                Button(
                    onClick = {
                        uiState.currentAudio?.let { audio ->
                            onNavigateToLesson(audio.lessonId.toString())
                        }
                    },
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "مشاهده درس",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}