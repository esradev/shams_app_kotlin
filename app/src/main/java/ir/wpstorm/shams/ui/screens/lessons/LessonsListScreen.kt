package ir.wpstorm.shams.ui.screens.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ir.wpstorm.shams.ui.components.GlobalError
import ir.wpstorm.shams.ui.components.GlobalLoading
import ir.wpstorm.shams.ui.components.LessonCard
import ir.wpstorm.shams.ui.theme.Emerald400
import ir.wpstorm.shams.ui.theme.Emerald50
import ir.wpstorm.shams.ui.theme.Emerald700
import ir.wpstorm.shams.ui.theme.Gray400
import ir.wpstorm.shams.ui.theme.Gray50
import ir.wpstorm.shams.ui.theme.Gray500
import ir.wpstorm.shams.ui.theme.Gray700
import ir.wpstorm.shams.ui.theme.Gray800
import ir.wpstorm.shams.ui.theme.Gray900
import ir.wpstorm.shams.viewmodel.CategoryViewModel
import ir.wpstorm.shams.viewmodel.CategoryViewModelFactory
import ir.wpstorm.shams.viewmodel.LessonsListViewModel
import ir.wpstorm.shams.viewmodel.LessonsListViewModelFactory
import androidx.compose.runtime.CompositionLocalProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsListScreen(
    categoryId: Int,
    onLessonClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ir.wpstorm.shams.ShamsApplication

    // Lessons ViewModel
    val lessonRepository = application.lessonRepository
    val lessonFactory = LessonsListViewModelFactory(lessonRepository)
    val lessonViewModel: LessonsListViewModel = viewModel(factory = lessonFactory)
    val lessonUiState by lessonViewModel.uiState.collectAsState()

    // Category ViewModel to get category details
    val categoryRepository = application.categoryRepository
    val categoryFactory = CategoryViewModelFactory(categoryRepository)
    val categoryViewModel: CategoryViewModel = viewModel(factory = categoryFactory)
    val categoryUiState by categoryViewModel.uiState.collectAsState()

    var isDescriptionExpanded by remember { mutableStateOf(false) }

    // Get the current category
    val currentCategory = categoryUiState.categories.find { it.id == categoryId }

    // Validate categoryId
    if (categoryId <= 0) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Scaffold { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    GlobalError(
                        type = "general",
                        message = "شناسه دسته‌بندی نامعتبر است",
                        onRetry = { onBack() }
                    )
                }
            }
        }
        return
    }

    LaunchedEffect(categoryId) {
        lessonViewModel.loadLessonsForCategory(categoryId)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentCategory?.name ?: "درس‌ها",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = if (MaterialTheme.colorScheme.background == Color.White) {
                Gray50
            } else {
                Gray900
            }
        ) { paddingValues ->
            when {
                lessonUiState.error != null -> {
                    GlobalError(
                        type = "network",
                        message = lessonUiState.error,
                        onRetry = { lessonViewModel.loadLessonsForCategory(categoryId) },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                lessonUiState.isLoading -> {
                    GlobalLoading(
                        message = "در حال بارگذاری درس‌ها...",
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Category header section
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
                            ) {
                                // Category title and badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = currentCategory?.name ?: "نام درس",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Teacher info and category badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "آیت الله سید محمدرضا حسینی آملی (حفظه الله)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Gray500,
                                        textAlign = TextAlign.Right
                                    )

                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Gray400,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    // Category badge
                                    Text(
                                        text = "درس خارج",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = if (MaterialTheme.colorScheme.surface == Color.White) {
                                            Emerald700
                                        } else {
                                            Emerald400
                                        },
                                        modifier = Modifier
                                            .background(
                                                color = if (MaterialTheme.colorScheme.surface == Color.White) {
                                                    Emerald50
                                                } else {
                                                    Emerald700.copy(alpha = 0.3f)
                                                },
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Expandable description section
                                if (currentCategory?.description?.isNotEmpty() == true) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    isDescriptionExpanded = !isDescriptionExpanded
                                                }
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isDescriptionExpanded) {
                                                    Icons.Default.KeyboardArrowUp
                                                } else {
                                                    Icons.Default.KeyboardArrowDown
                                                },
                                                contentDescription = if (isDescriptionExpanded) "بستن" else "باز کردن",
                                                tint = Gray700
                                            )

                                            Text(
                                                text = "در مورد درس",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 18.sp
                                                ),
                                                color = MaterialTheme.colorScheme.onBackground,
                                                textAlign = TextAlign.Right
                                            )
                                        }

                                        if (isDescriptionExpanded) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = currentCategory.description
                                                    .replace(Regex("<[^>]+>"), "")
                                                    .trim(),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    lineHeight = 24.sp
                                                ),
                                                color = Gray700,
                                                textAlign = TextAlign.Right,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                }

                                // Sessions header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "جلسات",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 18.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        textAlign = TextAlign.Right
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        // Lessons list
                        if (lessonUiState.lessons.isNotEmpty()) {
                            itemsIndexed(lessonUiState.lessons) { index, lesson ->
                                LessonCard(
                                    lesson = lesson,
                                    lessonNumber = index + 1,
                                    onClick = { onLessonClick(lesson.id) },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "هیچ درسی در این دسته یافت نشد",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Gray500,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}
