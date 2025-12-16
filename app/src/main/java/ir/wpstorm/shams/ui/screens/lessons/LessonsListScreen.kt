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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.wpstorm.shams.ui.components.GlobalError
import ir.wpstorm.shams.ui.components.GlobalLoading
import ir.wpstorm.shams.ui.components.LessonCard
import ir.wpstorm.shams.ui.theme.Emerald400
import ir.wpstorm.shams.ui.theme.Emerald50
import ir.wpstorm.shams.ui.theme.Emerald700
import ir.wpstorm.shams.ui.theme.Gray50
import ir.wpstorm.shams.ui.theme.Gray900
import ir.wpstorm.shams.viewmodel.CategoryViewModel
import ir.wpstorm.shams.viewmodel.CategoryViewModelFactory
import ir.wpstorm.shams.viewmodel.LessonsListViewModel
import ir.wpstorm.shams.viewmodel.LessonsListViewModelFactory

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

    // Category ViewModel
    val categoryRepository = application.categoryRepository
    val categoryFactory = CategoryViewModelFactory(categoryRepository)
    val categoryViewModel: CategoryViewModel = viewModel(factory = categoryFactory)
    val categoryUiState by categoryViewModel.uiState.collectAsState()

    // Progress Repository
    val progressRepository = application.progressRepository
    val completedLessons by progressRepository.getCompletedLessonsByCategory(categoryId).collectAsState(initial = emptyList())

    // Expandable description state
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    // Filter state
    var showFilterDialog by remember { mutableStateOf(false) }

    // Posts per page selector state
    var showPostsPerPageDropdown by remember { mutableStateOf(false) }

    // Pagination state
    val listState = rememberLazyListState()


    // Get the current category
    val currentCategory = categoryUiState.categories.find { it.id == categoryId }

    // Validate categoryId
    if (categoryId <= 0) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                GlobalError(
                    type = "general",
                    message = "شناسه دسته‌بندی نامعتبر است",
                    onRetry = { onBack() }
                )
            }
        }
        return
    }

    LaunchedEffect(categoryId) {
        lessonViewModel.loadLessonsForCategory(categoryId, 1)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (MaterialTheme.colorScheme.background == Color.White) {
                        Gray50
                    } else {
                        Gray900
                    }
                )
        ) {
            when {
                lessonUiState.error != null -> {
                    GlobalError(
                        type = "network",
                        message = lessonUiState.error,
                        onRetry = { lessonViewModel.retryLoading() }
                    )
                }

                lessonUiState.isLoading -> {
                    GlobalLoading(
                        message = "در حال بارگذاری درس‌ها..."
                    )
                }

                else -> {
                    // Main content with sticky bottom pagination
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Main scrollable content
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            state = listState
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Right
                                    )

                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
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
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                    // Left side: Posts per page and Filter
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Filter button
                                        IconButton(
                                            onClick = { showFilterDialog = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FilterList,
                                                contentDescription = "فیلتر کردن",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        // Posts per page selector
                                        Box {
                                            OutlinedButton(
                                                onClick = { showPostsPerPageDropdown = true },
                                                modifier = Modifier.height(36.dp)
                                            ) {
                                                Text(
                                                    text = "${lessonUiState.postsPerPage}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDropDown,
                                                    contentDescription = "انتخاب تعداد",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            DropdownMenu(
                                                expanded = showPostsPerPageDropdown,
                                                onDismissRequest = { showPostsPerPageDropdown = false }
                                            ) {
                                                listOf(10, 20, 30, 40, 50, 75, 100).forEach { count ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                text = "$count درس در صفحه",
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                        },
                                                        onClick = {
                                                            lessonViewModel.changePostsPerPage(count)
                                                            showPostsPerPageDropdown = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

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
                                val isCompleted = completedLessons.any { it.lessonId == lesson.id }
                                LessonCard(
                                    lesson = lesson,
                                    lessonNumber = index + 1,
                                    onClick = { onLessonClick(lesson.id) },
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    isCompleted = isCompleted
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Bottom spacing for sticky pagination
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // Minimal eye-catching pagination
                    if (lessonUiState.lessons.isNotEmpty() && lessonUiState.totalPages > 1) {
                        val canGoBack = lessonUiState.currentPage > 1
                        val canGoForward = lessonUiState.currentPage < lessonUiState.totalPages

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Previous button
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = if (canGoBack) {
                                            if (MaterialTheme.colorScheme.surface == Color.White) {
                                                Emerald700
                                            } else {
                                                Emerald400
                                            }
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = canGoBack) { lessonViewModel.goToPreviousPage() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "صفحه قبل",
                                    modifier = Modifier.size(20.dp),
                                    tint = if (canGoBack) {
                                        Color.White
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    }
                                )
                            }

                            // Page info with badge style
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${lessonUiState.currentPage}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "/",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "${lessonUiState.totalPages}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Next button
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = if (canGoForward) {
                                            if (MaterialTheme.colorScheme.surface == Color.White) {
                                                Emerald700
                                            } else {
                                                Emerald400
                                            }
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = canGoForward) { lessonViewModel.goToNextPage() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "صفحه بعد",
                                    modifier = Modifier.size(20.dp),
                                    tint = if (canGoForward) {
                                        Color.White
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

        // Filter Dialog
        if (showFilterDialog) {
            Dialog(onDismissRequest = { showFilterDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "مرتب سازی بر اساس",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Date options
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (lessonUiState.orderBy == "date" && lessonUiState.order == "desc"),
                                        onClick = {
                                            lessonViewModel.changeOrder("date", "desc")
                                            showFilterDialog = false
                                        }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "جدیدترین",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Right
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                RadioButton(
                                    selected = (lessonUiState.orderBy == "date" && lessonUiState.order == "desc"),
                                    onClick = {
                                        lessonViewModel.changeOrder("date", "desc")
                                        showFilterDialog = false
                                    }
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (lessonUiState.orderBy == "date" && lessonUiState.order == "asc"),
                                        onClick = {
                                            lessonViewModel.changeOrder("date", "asc")
                                            showFilterDialog = false
                                        }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "قدیمی‌ترین",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Right
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                RadioButton(
                                    selected = (lessonUiState.orderBy == "date" && lessonUiState.order == "asc"),
                                    onClick = {
                                        lessonViewModel.changeOrder("date", "asc")
                                        showFilterDialog = false
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showFilterDialog = false }
                            ) {
                                Text(
                                    text = "انصراف",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
