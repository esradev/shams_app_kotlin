package ir.wpstorm.shams.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.wpstorm.shams.data.repository.LessonRepository
import ir.wpstorm.shams.data.db.LessonEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LessonUiState(
    val lesson: LessonEntity? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class LessonViewModel(private val repository: LessonRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState

    fun loadLesson(lessonId: Int) {
        viewModelScope.launch {
            _uiState.value = LessonUiState(isLoading = true)
            try {
                val lesson = repository.getLessonById(lessonId)
                _uiState.value = LessonUiState(lesson = lesson, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = LessonUiState(error = e.message, isLoading = false)
            }
        }
    }
}

