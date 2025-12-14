package ir.wpstorm.shams.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.wpstorm.shams.data.repository.LessonRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchResultItem> = emptyList(),
    val error: String? = null
)

data class SearchResultItem(
    val id: Int,
    val title: String
)

class SearchViewModel(private val repository: LessonRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)

        // Cancel previous search
        searchJob?.cancel()

        if (newQuery.isBlank()) {
            _uiState.value = _uiState.value.copy(
                results = emptyList(),
                isLoading = false,
                error = null
            )
            return
        }

        // Debounce search with 500ms delay
        searchJob = viewModelScope.launch {
            delay(500)
            searchLessons(newQuery)
        }
    }

    private suspend fun searchLessons(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        try {
            val lessons = repository.searchLessons(query)
            val results = lessons.map { lesson ->
                SearchResultItem(
                    id = lesson.id,
                    title = lesson.title
                )
            }
            _uiState.value = _uiState.value.copy(
                results = results,
                isLoading = false,
                error = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                results = emptyList(),
                isLoading = false,
                error = e.message ?: "Search failed"
            )
        }
    }
}
