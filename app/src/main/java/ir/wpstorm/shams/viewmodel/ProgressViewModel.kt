package ir.wpstorm.shams.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ir.wpstorm.shams.data.db.CourseProgressEntity
import ir.wpstorm.shams.data.repository.CategoryRepository
import ir.wpstorm.shams.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProgressUiState(
    val courseProgress: List<CourseProgressWithCategory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CourseProgressWithCategory(
    val progress: CourseProgressEntity,
    val categoryName: String,
    val categoryDescription: String?,
    val progressPercentage: Int,
    val nextUncompletedLessonId: Int?
)

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadProgress()
    }

    fun loadProgress() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                progressRepository.getAllCourseProgress().collect { progressList ->
                    val progressWithCategories = progressList.map { progress ->
                        // Fetch category details
                        val category = categoryRepository.getCategoryById(progress.categoryId)

                        // Get first uncompleted lesson ID
                        val nextUncompletedLessonId = progressRepository.getFirstUncompletedLesson(progress.categoryId)

                        // Calculate percentage
                        val percentage = if (progress.totalLessons > 0) {
                            (progress.completedLessons * 100) / progress.totalLessons
                        } else {
                            0
                        }

                        CourseProgressWithCategory(
                            progress = progress,
                            categoryName = category?.name ?: "درس ${progress.categoryId}",
                            categoryDescription = category?.description,
                            progressPercentage = percentage,
                            nextUncompletedLessonId = nextUncompletedLessonId
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        courseProgress = progressWithCategories,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "خطا در بارگذاری پیشرفت"
                )
            }
        }
    }

    fun retryLoading() {
        loadProgress()
    }
}

class ProgressViewModelFactory(
    private val progressRepository: ProgressRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressViewModel(progressRepository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
