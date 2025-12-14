package ir.wpstorm.shams.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.wpstorm.shams.data.repository.DownloadedAudioRepository

class SettingsViewModelFactory(
    private val downloadedAudioRepository: DownloadedAudioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(downloadedAudioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
