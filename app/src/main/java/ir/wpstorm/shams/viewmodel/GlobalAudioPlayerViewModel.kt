package ir.wpstorm.shams.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ir.wpstorm.shams.data.db.DownloadedAudioEntity
import ir.wpstorm.shams.player.AudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

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

    private val _currentAudio = MutableStateFlow<DownloadedAudioEntity?>(null)
    private val _isMinimized = MutableStateFlow(false)

    val uiState: StateFlow<GlobalAudioPlayerUiState> = combine(
        audioPlayer.isPlaying,
        audioPlayer.currentPosition,
        audioPlayer.duration,
        audioPlayer.isLoaded,
        audioPlayer.playbackSpeed,
        _currentAudio,
        _isMinimized
    ) { flows ->
        val isPlaying = flows[0] as Boolean
        val position = flows[1] as Long
        val duration = flows[2] as Long
        val loaded = flows[3] as Boolean
        val speed = flows[4] as Float
        val audio = flows[5] as DownloadedAudioEntity?
        val minimized = flows[6] as Boolean

        GlobalAudioPlayerUiState(
            isPlaying = isPlaying,
            currentAudio = audio,
            isMinimized = minimized,
            currentPosition = position,
            duration = duration,
            isLoaded = loaded,
            playbackSpeed = speed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GlobalAudioPlayerUiState()
    )

    fun playAudio(audio: DownloadedAudioEntity) {
        _currentAudio.value = audio
        _isMinimized.value = true

        audioPlayer.prepare(audio.filePath)
        audioPlayer.play()
    }

    fun setCurrentAudio(audio: DownloadedAudioEntity) {
        _currentAudio.value = audio
        _isMinimized.value = true
    }

    fun setCurrentAudioAndPlay(audio: DownloadedAudioEntity) {
        _currentAudio.value = audio
        _isMinimized.value = true
        audioPlayer.prepare(audio.filePath)
        audioPlayer.play()
    }

    fun togglePlayPause() {
        if (uiState.value.isPlaying) {
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
        _isMinimized.value = true
    }

    fun maximize() {
        _isMinimized.value = false
    }

    fun stopAndClear() {
        audioPlayer.pause()
        _currentAudio.value = null
        _isMinimized.value = false
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
