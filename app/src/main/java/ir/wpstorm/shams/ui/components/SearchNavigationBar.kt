package ir.wpstorm.shams.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.wpstorm.shams.ui.theme.Emerald400
import ir.wpstorm.shams.ui.theme.Emerald50
import ir.wpstorm.shams.ui.theme.Emerald700

@Composable
fun SearchNavigationBar(
    searchQuery: String,
    currentMatchIndex: Int,
    totalMatches: Int,
    onPreviousMatch: () -> Unit,
    onNextMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (searchQuery.isBlank() || totalMatches == 0) return

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = if (MaterialTheme.colorScheme.surface == Color.White) {
                        Emerald50
                    } else {
                        Emerald700.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "جستجو: $searchQuery",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (MaterialTheme.colorScheme.surface == Color.White) {
                        Emerald700
                    } else {
                        Emerald400
                    },
                    textAlign = TextAlign.Right
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentMatchIndex + 1} از $totalMatches",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = if (MaterialTheme.colorScheme.surface == Color.White) {
                            Emerald700
                        } else {
                            Emerald400
                        }
                    )

                    IconButton(
                        onClick = onNextMatch,
                        enabled = currentMatchIndex < totalMatches - 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "مطابقت بعدی",
                            tint = if (MaterialTheme.colorScheme.surface == Color.White) {
                                Emerald700
                            } else {
                                Emerald400
                            }
                        )
                    }

                    IconButton(
                        onClick = onPreviousMatch,
                        enabled = currentMatchIndex > 0,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "مطابقت قبلی",
                            tint = if (MaterialTheme.colorScheme.surface == Color.White) {
                                Emerald700
                            } else {
                                Emerald400
                            }
                        )
                    }
                }
            }
        }
    }
}
