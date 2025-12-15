package ir.wpstorm.shams.player

import android.content.Context
import android.net.Uri
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioPlayer(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    private var updateJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var currentUri: String? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    fun prepare(uri: String) {
        // Don't re-prepare if the same audio is already loaded
        if (currentUri == uri && exoPlayer != null) {
            return
        }

        release()
        currentUri = uri
        exoPlayer = ExoPlayer.Builder(context).build().also { player ->
            player.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
            player.prepare()

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            _isLoaded.value = true
                            _duration.value = player.duration
                            startPositionUpdates()
                        }
                        Player.STATE_ENDED -> {
                            _isPlaying.value = false
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    if (isPlaying) {
                        startPositionUpdates()
                    }
                }
            })
        }
    }

    private fun startPositionUpdates() {
        updateJob?.cancel()
        updateJob = scope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentPosition.value = player.currentPosition
                    }
                }
                delay(100) // Update frequently for smooth UI
            }
        }
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        _currentPosition.value = position
    }

    fun forward(ms: Long = 30000) {
        exoPlayer?.let {
            val newPosition = (it.currentPosition + ms).coerceAtMost(it.duration)
            seekTo(newPosition)
        }
    }

    fun rewind(ms: Long = 30000) {
        exoPlayer?.let {
            val newPosition = (it.currentPosition - ms).coerceAtLeast(0)
            seekTo(newPosition)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        if (speed > 0f && speed <= 3f) {
            exoPlayer?.setPlaybackSpeed(speed)
            _playbackSpeed.value = speed
        }
    }

    fun release() {
        updateJob?.cancel()
        updateJob = null
        exoPlayer?.release()
        exoPlayer = null
        currentUri = null
        _isLoaded.value = false
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
        _playbackSpeed.value = 1f
    }
}
