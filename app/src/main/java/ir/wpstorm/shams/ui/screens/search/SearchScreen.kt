package ir.wpstorm.shams.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.wpstorm.shams.data.db.LessonEntity
import ir.wpstorm.shams.ui.components.EmptyState
import ir.wpstorm.shams.ui.components.GlobalError
import ir.wpstorm.shams.ui.components.GlobalLoading
import ir.wpstorm.shams.ui.components.LessonCard
import ir.wpstorm.shams.ui.theme.Gray50
import ir.wpstorm.shams.ui.theme.Gray900
import ir.wpstorm.shams.viewmodel.SearchViewModel
import ir.wpstorm.shams.viewmodel.SearchViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onLessonClick: (Int, String) -> Unit, // Added query parameter for highlighting
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ir.wpstorm.shams.ShamsApplication
    val repository = application.lessonRepository
    val factory = SearchViewModelFactory(repository)
    val viewModel: SearchViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (MaterialTheme.colorScheme.background == Color.White) {
                        Gray50
                    } else {
                        Gray900
                    }
                ),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "جستجو در درس‌ها",
                            textAlign = TextAlign.Right
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        if (MaterialTheme.colorScheme.background == Color.White) {
                            Gray50
                        } else {
                            Gray900
                        }
                    )
            ) {
                // Search input
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = viewModel::onQueryChange,
                    placeholder = {
                        Text(
                            "عنوان درس، محتوا یا کلمه کلیدی را جستجو کنید...",
                            textAlign = TextAlign.Right
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجو"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Search results
                when {
                    uiState.isLoading -> {
                        GlobalLoading(
                            message = "در حال جستجو..."
                        )
                    }

                    uiState.error != null -> {
                        GlobalError(
                            type = "general",
                            message = uiState.error,
                            onRetry = { viewModel.onQueryChange(uiState.query) }
                        )
                    }

                    uiState.query.isBlank() -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    text = "راهنمای جستجو",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                )

                                val tips = listOf(
                                    "عنوان درس را جستجو کنید",
                                    "از کلمات کلیدی محتوا استفاده کنید",
                                    "نام استاد را وارد کنید",
                                    "موضوع درس را تایپ کنید"
                                )

                                tips.forEach { tip ->
                                    Text(
                                        text = "• $tip",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    uiState.results.isEmpty() -> {
                        EmptyState(
                            message = "نتیجه‌ای برای «${uiState.query}» یافت نشد",
                            onRetry = { viewModel.onQueryChange(uiState.query) }
                        )
                    }

                    else -> {
                        Column {
                            Text(
                                text = "${uiState.results.size} نتیجه برای «${uiState.query}»",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Right,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(uiState.results) { index, result ->
                                    // Convert SearchResultItem to LessonEntity for the card
                                    val lessonEntity = LessonEntity(
                                        id = result.id,
                                        title = result.title,
                                        content = result.excerpt ?: "",
                                        audioUrl = null,
                                        categoryId = null
                                    )

                                    LessonCard(
                                        lesson = lessonEntity,
                                        lessonNumber = index + 1,
                                        onClick = { onLessonClick(result.id, uiState.query) }
                                    )
                                }

                                // Bottom spacing
                                item {
                                    Box(modifier = Modifier.padding(bottom = 80.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

