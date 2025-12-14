package ir.wpstorm.shams.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ir.wpstorm.shams.ui.components.CourseCard
import ir.wpstorm.shams.ui.components.GlobalError
import ir.wpstorm.shams.ui.components.GlobalLoading
import ir.wpstorm.shams.viewmodel.CategoryViewModel
import ir.wpstorm.shams.viewmodel.CategoryViewModelFactory

@Composable
fun CategoryScreen(
    onCategoryClick: (Int) -> Unit,
    onSearchClick: () -> Unit, // Kept for potential future use
    onSettingsClick: () -> Unit // Kept for potential future use
) {
    val context = LocalContext.current
    val application = context.applicationContext as ir.wpstorm.shams.ShamsApplication
    val repository = application.categoryRepository
    val factory = CategoryViewModelFactory(repository)
    val viewModel: CategoryViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    // Status bar styling
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (isDarkTheme) Color(0xFF1C1917) else Color(0xFFF0FDF4),
            darkIcons = !isDarkTheme
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.error != null -> {
                    GlobalError(
                        type = "network",
                        message = uiState.error,
                        onRetry = { viewModel.retryLoading() }
                    )
                }

                uiState.isLoading -> {
                    GlobalLoading(
                        message = "در حال آماده سازی برنامه"
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentPadding = PaddingValues(bottom = 80.dp) // Add bottom padding for tab bar
                    ) {
                        item {
                            Column(
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Text(
                                    text = "تمام دروس خارج",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 20.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 32.dp)
                                )
                            }
                        }

                        items(
                            uiState.categories
                        ) { category ->
                            CourseCard(
                                course = category,
                                onClick = { onCategoryClick(category.id) },
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                            )
                        }

                        // Add bottom padding for better scrolling experience
                        item {
                            Box(modifier = Modifier.padding(bottom = 24.dp))
                        }
                    }
                }
            }
        }
    }
}
