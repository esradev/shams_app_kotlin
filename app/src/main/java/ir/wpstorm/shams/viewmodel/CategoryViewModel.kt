package ir.wpstorm.shams.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.wpstorm.shams.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryItem> = emptyList(),
    val error: String? = null
)

data class CategoryItem(
    val id: Int,
    val name: String,
    val description: String = "",
    val parent: Int = 0,
    val count: Int = 0
)

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState(isLoading = true))
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = CategoryUiState(isLoading = true)
            try {
                repository.getCategories().collect { categories ->
                    _uiState.value = CategoryUiState(
                        isLoading = false,
                        categories = categories
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CategoryUiState(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun retryLoading() {
        loadCategories()
    }
}
