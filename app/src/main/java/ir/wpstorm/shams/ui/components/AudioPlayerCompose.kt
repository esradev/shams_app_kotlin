package ir.wpstorm.shams.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.wpstorm.shams.player.AudioPlayer
import ir.wpstorm.shams.ui.theme.Emerald400
import ir.wpstorm.shams.ui.theme.Emerald700
import ir.wpstorm.shams.ui.theme.Gray400
import ir.wpstorm.shams.ui.theme.Gray50
import ir.wpstorm.shams.ui.theme.Gray700

@Composable
fun AudioPlayerCompose(
    audioPlayer: AudioPlayer,
    postTitle: String,
    onDownload: () -> Unit = {},
    onDelete: () -> Unit = {},
    isDownloading: Boolean = false,
    downloadProgress: Float = 0f,
    isDownloaded: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isPlaying by audioPlayer.isPlaying
    val currentPosition by audioPlayer.currentPosition
    val duration by audioPlayer.duration
    val isLoaded by audioPlayer.isLoaded
    val playbackSpeed by audioPlayer.playbackSpeed

    var showDeleteDialog by remember { mutableStateOf(false) }

    fun formatTime(timeMs: Long): String {
        val minutes = (timeMs / 1000) / 60
        val seconds = (timeMs / 1000) % 60
        return "%d:%02d".format(minutes, seconds)
    }

    fun togglePlaybackSpeed() {
        val newSpeed = when (playbackSpeed) {
            1f -> 1.25f
            1.25f -> 1.5f
            1.5f -> 2f
            2f -> 0.75f
            else -> 1f
        }
        audioPlayer.setPlaybackSpeed(newSpeed)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Progress bar section
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(duration),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            color = Gray700,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(48.dp)
                        )

                        Slider(
                            value = if (duration > 0) currentPosition.toFloat() else 0f,
                            onValueChange = { newValue ->
                                if (duration > 0) {
                                    audioPlayer.seekTo(newValue.toLong())
                                }
                            },
                            valueRange = 0f..(if (duration > 0) duration.toFloat() else 1f),
                            enabled = duration > 0,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = Emerald700,
                                activeTrackColor = Emerald700,
                                inactiveTrackColor = Gray400
                            )
                        )

                        Text(
                            text = formatTime(currentPosition),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            color = Gray700,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main controls section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Playback speed button
                    Box(
                        modifier = Modifier
                            .background(
                                color = Emerald700.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { togglePlaybackSpeed() }
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${playbackSpeed}×",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                ),
                                color = Emerald700
                            )
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "سرعت پخش",
                                modifier = Modifier.size(18.dp),
                                tint = Emerald700
                            )
                        }
                    }

                    // Backward button
                    Box(
                        modifier = Modifier
                            .clickable { audioPlayer.rewind() }
                            .background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "30 ثانیه عقب",
                            modifier = Modifier.size(24.dp),
                            tint = Gray700
                        )
                    }

                    // Play/Pause button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                color = if (isLoaded) Emerald700 else Gray400
                            )
                            .clickable(enabled = isLoaded) {
                                if (isPlaying) {
                                    audioPlayer.pause()
                                } else {
                                    audioPlayer.play()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isLoaded) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "مکث" else "پخش",
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }

                    // Forward button
                    Box(
                        modifier = Modifier
                            .clickable { audioPlayer.forward() }
                            .background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "30 ثانیه جلو",
                            modifier = Modifier.size(24.dp),
                            tint = Gray700
                        )
                    }

                    // Download/Delete button
                    Box(
                        modifier = Modifier
                            .background(
                                color = when {
                                    isDownloaded -> Color(0x1A6B7280) // Gray with alpha
                                    isDownloading -> Emerald700.copy(alpha = 0.1f)
                                    else -> Color(0x1A6B7280)
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable(enabled = !isDownloading) {
                                if (isDownloaded) {
                                    showDeleteDialog = true
                                } else {
                                    onDownload()
                                }
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isDownloading -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${(downloadProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        ),
                                        color = Emerald700
                                    )
                                }
                            }
                            isDownloaded -> {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف فایل دانلود شده",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFDC2626) // Red color
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = "دانلود صوت",
                                    modifier = Modifier.size(16.dp),
                                    tint = Gray700
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "حذف فایل دانلود شده",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Right
                )
            },
            text = {
                Text(
                    text = "این درس قبلاً دانلود شده است. آیا می‌خواهید فایل دانلود شده را حذف کنید؟",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Right
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("حذف فایل")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("انصراف")
                }
            }
        )
    }
}
