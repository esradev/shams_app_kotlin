package ir.wpstorm.shams.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.wpstorm.shams.data.db.DownloadedAudioEntity
import ir.wpstorm.shams.data.repository.DownloadedAudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class SettingsUiState(
    val isLoading: Boolean = false,
    val downloadedAudios: List<DownloadedAudioEntity> = emptyList(),
    val totalSize: Long = 0L,
    val offlineMode: Boolean = false,
    val autoDownload: Boolean = false,
    val error: String? = null
)

class SettingsViewModel(
    private val downloadedAudioRepository: DownloadedAudioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadDownloadedAudios()
    }

    private fun loadDownloadedAudios() {
        viewModelScope.launch {
            try {
                downloadedAudioRepository.getAllDownloadedAudios().collect { audios ->
                    _uiState.value = _uiState.value.copy(
                        downloadedAudios = audios,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteAudio(audio: DownloadedAudioEntity) {
        viewModelScope.launch {
            try {
                // Delete physical file first
                val file = File(audio.filePath)
                if (file.exists()) {
                    file.delete()
                }

                // Remove from database
                downloadedAudioRepository.deleteDownloadedAudio(audio)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "خطا در حذف فایل: ${e.message}"
                )
            }
        }
    }

    fun clearAllDownloads() {
        viewModelScope.launch {
            try {
                // Get all downloaded audios first
                val currentAudios = _uiState.value.downloadedAudios

                // Delete physical files
                currentAudios.forEach { audio ->
                    val file = File(audio.filePath)
                    if (file.exists()) {
                        file.delete()
                    }
                }

                // Clear database
                downloadedAudioRepository.clearAllDownloadedAudios()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "خطا در حذف فایل‌ها: ${e.message}"
                )
            }
        }
    }

    fun updatePlayInfo(lessonId: Int) {
        viewModelScope.launch {
            try {
                downloadedAudioRepository.updatePlayInfo(lessonId, System.currentTimeMillis())
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "خطا در به‌روزرسانی اطلاعات پخش: ${e.message}"
                )
            }
        }
    }

    fun setOfflineMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(offlineMode = enabled)
    }

    fun setAutoDownload(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoDownload = enabled)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
