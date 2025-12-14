package ir.wpstorm.shams.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ir.wpstorm.shams.data.db.DownloadedAudioEntity
import ir.wpstorm.shams.player.AudioPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class GlobalAudioPlayerUiState(
    val isPlaying: Boolean = false,
    val currentAudio: DownloadedAudioEntity? = null,
    val isMinimized: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isLoaded: Boolean = false,
    val playbackSpeed: Float = 1f
)

class GlobalAudioPlayerViewModel(
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _uiState = mutableStateOf(GlobalAudioPlayerUiState())
    val uiState: State<GlobalAudioPlayerUiState> = _uiState

    init {
        // Observe audio player state changes
        viewModelScope.launch {
            // Use a simple polling approach to update UI state
            // In a real app, you might want to use StateFlow or other reactive patterns
            observeAudioPlayerState()
        }
    }

    private suspend fun observeAudioPlayerState() {
        while (true) {
            val currentState = _uiState.value
            _uiState.value = currentState.copy(
                isPlaying = audioPlayer.isPlaying.value,
                currentPosition = audioPlayer.currentPosition.value,
                duration = audioPlayer.duration.value,
                isLoaded = audioPlayer.isLoaded.value,
                playbackSpeed = audioPlayer.playbackSpeed.value
                // Preserve currentAudio and isMinimized values
            )
            delay(100)
        }
    }

    fun playAudio(audio: DownloadedAudioEntity) {
        // First set the state to show the mini player
        _uiState.value = _uiState.value.copy(
            currentAudio = audio,
            isMinimized = true  // Ensure mini player shows
        )

        // Then prepare and play the audio
        audioPlayer.prepare(audio.filePath)
        audioPlayer.play()

        // Force update the UI state again to ensure it's reflected
        _uiState.value = _uiState.value.copy(
            currentAudio = audio,
            isMinimized = true
        )
    }

    /**
     * Sets the current audio metadata without re-preparing the player.
     * Use this when the audio is already playing and you just want to update the mini player display.
     */
    fun setCurrentAudio(audio: DownloadedAudioEntity) {
        _uiState.value = _uiState.value.copy(
            currentAudio = audio,
            isMinimized = true  // Show mini player
        )
    }

    /**
     * Sets current audio and starts playing it.
     * Use this when you want to set and play audio from a lesson.
     */
    fun setCurrentAudioAndPlay(audio: DownloadedAudioEntity) {
        _uiState.value = _uiState.value.copy(
            currentAudio = audio,
            isMinimized = true  // Show mini player
        )
        // Prepare and play the audio
        audioPlayer.prepare(audio.filePath)
        audioPlayer.play()
    }

    fun togglePlayPause() {
        if (_uiState.value.isPlaying) {
            audioPlayer.pause()
        } else {
            audioPlayer.play()
        }
    }

    fun pause() {
        audioPlayer.pause()
    }

    fun seekTo(position: Long) {
        audioPlayer.seekTo(position)
    }

    fun forward(ms: Long = 30000) {
        audioPlayer.forward(ms)
    }

    fun rewind(ms: Long = 30000) {
        audioPlayer.rewind(ms)
    }

    fun setPlaybackSpeed(speed: Float) {
        audioPlayer.setPlaybackSpeed(speed)
    }

    fun minimize() {
        _uiState.value = _uiState.value.copy(isMinimized = true)
    }

    fun maximize() {
        _uiState.value = _uiState.value.copy(isMinimized = false)
    }

    fun stopAndClear() {
        audioPlayer.pause()
        _uiState.value = _uiState.value.copy(
            currentAudio = null,
            isMinimized = false
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}

class GlobalAudioPlayerViewModelFactory(
    private val audioPlayer: AudioPlayer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GlobalAudioPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GlobalAudioPlayerViewModel(audioPlayer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
