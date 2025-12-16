package ir.wpstorm.shams.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.wpstorm.shams.ui.theme.Emerald400
import ir.wpstorm.shams.ui.theme.Emerald700
import ir.wpstorm.shams.ui.theme.Gray50
import ir.wpstorm.shams.ui.theme.Gray700
import ir.wpstorm.shams.ui.theme.Gray900
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniAudioPlayer(
    uiState: GlobalAudioPlayerUiState,
    onPlayPauseClick: () -> Unit,
    onCloseClick: () -> Unit,
    onPlayerClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onDownload: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSpeedChange: () -> Unit = {},
    isDownloaded: Boolean = false,
    isDownloading: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Only show if there's an active audio
    if (uiState.currentAudio == null) return

    // Animated progress for smooth transitions
    val animatedProgress = animateFloatAsState(
        targetValue = if (uiState.duration > 0) {
            (uiState.currentPosition.toFloat() / uiState.duration.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        },
        animationSpec = tween(durationMillis = 100),
        label = "progress_animation"
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    spotColor = Color.Black.copy(alpha = 0.1f),
                    ambientColor = Color.Black.copy(alpha = 0.05f)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = Emerald700.copy(alpha = 0.1f)),
                    onClick = { onPlayerClick() }
                ),
            color = if (MaterialTheme.colorScheme.surface == Color.White) {
                Color.White
            } else {
                Gray900
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Interactive progress slider at top (Telegram style)
                Slider(
                    value = if (uiState.duration > 0) uiState.currentPosition.toFloat() else 0f,
                    onValueChange = { newValue ->
                        onSeek(newValue.toLong())
                    },
                    valueRange = 0f..(if (uiState.duration > 0) uiState.duration.toFloat() else 1f),
                    enabled = uiState.duration > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = Emerald700,
                        inactiveTrackColor = if (MaterialTheme.colorScheme.surface == Color.White) {
                            Gray50
                        } else {
                            Gray700.copy(alpha = 0.2f)
                        },
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = Emerald700.copy(alpha = 0.5f),
                        disabledInactiveTrackColor = Gray700.copy(alpha = 0.2f)
                    ),
                    thumb = { /* Empty thumb for minimal look */ }
                )

                // Main player content
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play/Pause button - Telegram style floating action button
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false, radius = 24.dp),
                                enabled = uiState.isLoaded,
                                onClick = { onPlayPauseClick() }
                            ),
                        shape = CircleShape,
                        color = if (uiState.isLoaded) Emerald700 else Gray700.copy(alpha = 0.3f),
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (uiState.isPlaying) "توقف" else "پخش",
                                modifier = Modifier.size(22.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Audio title and info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = uiState.currentAudio?.title ?: "در حال پخش...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Time display and playback speed
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Playback speed display
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { onSpeedChange() }
                                    .background(Emerald700.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "سرعت پخش",
                                    modifier = Modifier.size(12.dp),
                                    tint = Emerald700
                                )
                                Text(
                                    text = "${uiState.playbackSpeed}x",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = Emerald700
                                )
                            }

                            // Time display
                            val currentTime = formatTime(uiState.currentPosition)
                            val totalTime = formatTime(uiState.duration)
                            Text(
                                text = "$currentTime / $totalTime",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Left,
                                modifier = Modifier.alpha(0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Action buttons row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Close button
                        IconButton(
                            onClick = { onCloseClick() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن پخش‌کننده",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(timeMs: Long): String {
    val minutes = (timeMs / 1000) / 60
    val seconds = (timeMs / 1000) % 60
    return "%d:%02d".format(minutes, seconds)
}
