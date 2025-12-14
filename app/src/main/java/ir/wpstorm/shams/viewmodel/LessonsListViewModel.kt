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
    val error: String? = null
)

class LessonsListViewModel(private val repository: LessonRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonsListUiState())
    val uiState: StateFlow<LessonsListUiState> = _uiState

    fun loadLessonsForCategory(categoryId: Int) {
        viewModelScope.launch {
            try {
                Log.d("LessonsListViewModel", "Loading lessons for category: $categoryId")
                _uiState.value = LessonsListUiState(isLoading = true)
                val lessons = repository.getLessonsByCategory(categoryId)
                Log.d("LessonsListViewModel", "Loaded ${lessons.size} lessons")
                _uiState.value = LessonsListUiState(lessons = lessons, isLoading = false)
            } catch (e: Exception) {
                Log.e("LessonsListViewModel", "Error loading lessons: ${e.message}", e)
                _uiState.value = LessonsListUiState(
                    error = e.message ?: "Failed to load lessons",
                    isLoading = false
                )
            }
        }
    }
}
