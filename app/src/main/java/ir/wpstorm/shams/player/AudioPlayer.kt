package ir.wpstorm.shams.player

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import kotlinx.coroutines.*

class AudioPlayer(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentPosition = MutableLiveData(0L)
    val currentPosition: LiveData<Long> = _currentPosition

    private val _duration = MutableLiveData(0L)
    val duration: LiveData<Long> = _duration

    fun prepare(uri: String) {
        release()
        exoPlayer = ExoPlayer.Builder(context).build().also { player ->
            player.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
            player.prepare()
            _duration.value = player.duration

            // Update current position periodically
            scope.launch {
                while (isActive) {
                    _currentPosition.value = player.currentPosition
                    delay(500)
                }
            }
        }
    }

    fun play() {
        exoPlayer?.play()
        _isPlaying.value = true
    }

    fun pause() {
        exoPlayer?.pause()
        _isPlaying.value = false
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun forward(ms: Long = 30000) {
        exoPlayer?.let { seekTo(it.currentPosition + ms) }
    }

    fun rewind(ms: Long = 30000) {
        exoPlayer?.let { seekTo(it.currentPosition - ms) }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        scope.cancel()
    }
}
