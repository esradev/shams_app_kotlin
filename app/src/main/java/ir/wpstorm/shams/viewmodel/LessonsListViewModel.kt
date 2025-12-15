package ir.wpstorm.shams.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.wpstorm.shams.data.repository.LessonRepository
import ir.wpstorm.shams.data.db.LessonEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LessonsListUiState(
    val lessons: List<LessonEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val isLoadingMore: Boolean = false,
    val orderBy: String = "date",
    val order: String = "desc"
)

class LessonsListViewModel(private val repository: LessonRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonsListUiState())
    val uiState: StateFlow<LessonsListUiState> = _uiState

    private var currentCategoryId: Int? = null

    fun loadLessonsForCategory(categoryId: Int) {
        currentCategoryId = categoryId
        viewModelScope.launch {
            try {
                Log.d("LessonsListViewModel", "Loading lessons for category: $categoryId")
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    lessons = emptyList(),
                    currentPage = 1,
                    hasMorePages = true,
                    error = null
                )

                val lessons = repository.getLessonsByCategory(
                    categoryId = categoryId,
                    page = 1,
                    orderBy = _uiState.value.orderBy,
                    order = _uiState.value.order
                )
                Log.d("LessonsListViewModel", "Loaded ${lessons.size} lessons")

                _uiState.value = _uiState.value.copy(
                    lessons = lessons,
                    isLoading = false,
                    hasMorePages = lessons.size >= 20 // If we got 20 items, there might be more
                )
            } catch (e: Exception) {
                Log.e("LessonsListViewModel", "Error loading lessons: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load lessons",
                    isLoading = false
                )
            }
        }
    }

    fun loadNextPage() {
        val categoryId = currentCategoryId ?: return
        val currentState = _uiState.value

        if (currentState.isLoadingMore || !currentState.hasMorePages) return

        viewModelScope.launch {
            try {
                Log.d("LessonsListViewModel", "Loading page ${currentState.currentPage + 1}")
                _uiState.value = currentState.copy(isLoadingMore = true)

                val nextPage = currentState.currentPage + 1
                val newLessons = repository.getLessonsByCategory(
                    categoryId = categoryId,
                    page = nextPage,
                    orderBy = currentState.orderBy,
                    order = currentState.order
                )
                Log.d("LessonsListViewModel", "Loaded ${newLessons.size} more lessons")

                _uiState.value = currentState.copy(
                    lessons = currentState.lessons + newLessons,
                    currentPage = nextPage,
                    hasMorePages = newLessons.size >= 20,
                    isLoadingMore = false
                )
            } catch (e: Exception) {
                Log.e("LessonsListViewModel", "Error loading more lessons: ${e.message}", e)
                _uiState.value = currentState.copy(
                    error = e.message ?: "Failed to load more lessons",
                    isLoadingMore = false
                )
            }
        }
    }

    fun changeOrder(orderBy: String = "date", order: String = "desc") {
        val categoryId = currentCategoryId ?: return

        _uiState.value = _uiState.value.copy(
            orderBy = orderBy,
            order = order
        )

        // Reload with new order
        loadLessonsForCategory(categoryId)
    }

    fun retryLoading() {
        currentCategoryId?.let { categoryId ->
            loadLessonsForCategory(categoryId)
        }
    }
}
