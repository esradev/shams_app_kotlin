package ir.wpstorm.shams.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import ir.wpstorm.shams.ui.theme.Gray400
import ir.wpstorm.shams.ui.theme.Gray600

@Composable
fun AppHeader(
    title: String = "",
    showBackButton: Boolean = false,
    isHome: Boolean = false,
    showSearchIcon: Boolean = true,
    onBackClick: () -> Unit = {},
    onThemeToggle: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            tonalElevation = 2.dp
        ) {
            // Add gradient background for modern look
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isDarkTheme) {
                                listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                                )
                            } else {
                                listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            }
                        )
                    )
            ) {
                Column {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Action buttons section (left side in RTL)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Search Icon
                            if (showSearchIcon) {
                                Card(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clickable { onSearchClick() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isDarkTheme) {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 2.dp
                                    )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "جستجو",
                                            tint = if (isDarkTheme) Emerald400 else Emerald700,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            // Theme Toggle Button
                            Card(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { onThemeToggle() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkTheme) {
                                        Color(0xFF44403C)
                                    } else {
                                        Color(0xFFF5F5F4)
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 3.dp
                                )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = if (isDarkTheme) "حالت روشن" else "حالت تاریک",
                                        tint = if (isDarkTheme) Color(0xFFFFC107) else Color(0xFF1565C0),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Back Button (conditional)
                            if (showBackButton) {
                                Card(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clickable { onBackClick() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isDarkTheme) {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 2.dp
                                    )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "بازگشت",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Title Section (right side in RTL)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            if (isHome) {
                                // Home page brand with modern styling
                                Text(
                                    text = "شمس المعارف",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 26.sp
                                    ),
                                    color = if (isDarkTheme) Emerald400 else Emerald700,
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = "دروس خارج آیت الله حسینی آملی (حفظه الله)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = if (isDarkTheme) Gray400 else Gray600,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.padding(top = 2.dp),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                // Other pages title with modern styling
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.End,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
