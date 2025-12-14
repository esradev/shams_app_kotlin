package ir.wpstorm.shams.player

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.*

class AudioPlayer(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    private var updateJob: Job? = null

    // Compose State for better integration
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _currentPosition = mutableStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private val _duration = mutableStateOf(0L)
    val duration: State<Long> = _duration

    private val _isLoaded = mutableStateOf(false)
    val isLoaded: State<Boolean> = _isLoaded

    private val _playbackSpeed = mutableStateOf(1f)
    val playbackSpeed: State<Float> = _playbackSpeed

    // LiveData for backward compatibility
    private val _isPlayingLiveData = MutableLiveData(false)
    val isPlayingLiveData: LiveData<Boolean> = _isPlayingLiveData

    private val _currentPositionLiveData = MutableLiveData(0L)
    val currentPositionLiveData: LiveData<Long> = _currentPositionLiveData

    private val _durationLiveData = MutableLiveData(0L)
    val durationLiveData: LiveData<Long> = _durationLiveData

    fun prepare(uri: String) {
        release()
        exoPlayer = ExoPlayer.Builder(context).build().also { player ->
            player.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
            player.prepare()

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            _isLoaded.value = true
                            _duration.value = player.duration
                            _durationLiveData.value = player.duration
                            startPositionUpdates()
                        }
                        Player.STATE_ENDED -> {
                            _isPlaying.value = false
                            _isPlayingLiveData.value = false
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    _isPlayingLiveData.value = isPlaying
                }
            })
        }
    }

    private fun startPositionUpdates() {
        updateJob?.cancel()
        updateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && _isLoaded.value) {
                exoPlayer?.let { player ->
                    val position = player.currentPosition
                    _currentPosition.value = position
                    _currentPositionLiveData.value = position
                }
                delay(100) // Update more frequently for smoother UI
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
        if (speed > 0f && speed <= 3f) { // Reasonable speed limits
            exoPlayer?.setPlaybackSpeed(speed)
            _playbackSpeed.value = speed
        }
    }

    fun release() {
        updateJob?.cancel()
        updateJob = null
        exoPlayer?.release()
        exoPlayer = null
        _isLoaded.value = false
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
        _playbackSpeed.value = 1f
        _isPlayingLiveData.value = false
        _currentPositionLiveData.value = 0L
        _durationLiveData.value = 0L
    }
}
