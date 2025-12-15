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
    val totalPages: Int = 1,
    val postsPerPage: Int = 20,
    val totalPosts: Int = 0,
    val orderBy: String = "date",
    val order: String = "desc"
)

class LessonsListViewModel(private val repository: LessonRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonsListUiState())
    val uiState: StateFlow<LessonsListUiState> = _uiState

    private var currentCategoryId: Int? = null

    fun loadLessonsForCategory(categoryId: Int, page: Int = 1) {
        currentCategoryId = categoryId
        viewModelScope.launch {
            try {
                Log.d("LessonsListViewModel", "Loading lessons for category: $categoryId, page: $page")
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                val currentState = _uiState.value
                val lessons = repository.getLessonsByCategory(
                    categoryId = categoryId,
                    page = page,
                    perPage = currentState.postsPerPage,
                    orderBy = currentState.orderBy,
                    order = currentState.order
                )
                Log.d("LessonsListViewModel", "Loaded ${lessons.size} lessons")

                // Get total count for pagination
                val totalCount = repository.getTotalLessonsCount(categoryId)
                val totalPages = kotlin.math.ceil(totalCount.toDouble() / currentState.postsPerPage).toInt()

                _uiState.value = currentState.copy(
                    lessons = lessons,
                    isLoading = false,
                    currentPage = page,
                    totalPages = totalPages,
                    totalPosts = totalCount
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

    fun goToPage(page: Int) {
        val categoryId = currentCategoryId ?: return
        val currentState = _uiState.value

        if (page < 1 || page > currentState.totalPages || page == currentState.currentPage) return

        loadLessonsForCategory(categoryId, page)
    }

    fun goToNextPage() {
        val currentState = _uiState.value
        if (currentState.currentPage < currentState.totalPages) {
            goToPage(currentState.currentPage + 1)
        }
    }

    fun goToPreviousPage() {
        val currentState = _uiState.value
        if (currentState.currentPage > 1) {
            goToPage(currentState.currentPage - 1)
        }
    }

    fun changePostsPerPage(postsPerPage: Int) {
        val categoryId = currentCategoryId ?: return

        _uiState.value = _uiState.value.copy(
            postsPerPage = postsPerPage,
            currentPage = 1 // Reset to first page when changing posts per page
        )

        // Reload with new posts per page
        loadLessonsForCategory(categoryId, 1)
    }

    fun changeOrder(orderBy: String = "date", order: String = "desc") {
        val categoryId = currentCategoryId ?: return
        val currentPage = _uiState.value.currentPage

        _uiState.value = _uiState.value.copy(
            orderBy = orderBy,
            order = order
        )

        // Reload current page with new order
        loadLessonsForCategory(categoryId, currentPage)
    }

    fun retryLoading() {
        currentCategoryId?.let { categoryId ->
            loadLessonsForCategory(categoryId)
        }
    }
}
