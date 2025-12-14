package ir.wpstorm.shams.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.wpstorm.shams.data.repository.LessonRepository

class LessonViewModelFactory(private val repository: LessonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LessonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
