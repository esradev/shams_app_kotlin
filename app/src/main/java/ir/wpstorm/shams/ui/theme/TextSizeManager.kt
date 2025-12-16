package ir.wpstorm.shams.ui.theme

import android.content.Context
import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore extension
private val Context.textSizeDataStore: DataStore<Preferences> by preferencesDataStore(name = "text_size_preferences")

// Text size scale options
enum class TextSizeScale(val scale: Float, val label: String) {
    EXTRA_SMALL(0.85f, "خیلی کوچک"),
    SMALL(0.92f, "کوچک"),
    NORMAL(1.0f, "عادی"),
    LARGE(1.08f, "بزرگ"),
    EXTRA_LARGE(1.15f, "خیلی بزرگ"),
    HUGE(1.25f, "بسیار بزرگ");

    companion object {
        fun fromScale(scale: Float): TextSizeScale {
            return values().minByOrNull { kotlin.math.abs(it.scale - scale) } ?: NORMAL
        }

        fun next(current: TextSizeScale): TextSizeScale {
            val currentIndex = values().indexOf(current)
            return values().getOrElse(currentIndex + 1) { values().first() }
        }

        fun previous(current: TextSizeScale): TextSizeScale {
            val currentIndex = values().indexOf(current)
            return values().getOrElse(currentIndex - 1) { values().last() }
        }
    }
}

class TextSizeManager(private val context: Context) {
    private val TEXT_SCALE_KEY = floatPreferencesKey("text_scale")

    val textScale: Flow<Float> = context.textSizeDataStore.data
        .map { preferences ->
            preferences[TEXT_SCALE_KEY] ?: TextSizeScale.NORMAL.scale
        }

    suspend fun setTextScale(scale: Float) {
        context.textSizeDataStore.edit { preferences ->
            preferences[TEXT_SCALE_KEY] = scale
        }
    }

    suspend fun increaseTextSize() {
        val currentScale = textScale.map { TextSizeScale.fromScale(it) }
        currentScale.collect { current ->
            setTextScale(TextSizeScale.next(current).scale)
            return@collect
        }
    }

    suspend fun decreaseTextSize() {
        val currentScale = textScale.map { TextSizeScale.fromScale(it) }
        currentScale.collect { current ->
            setTextScale(TextSizeScale.previous(current).scale)
            return@collect
        }
    }
}

@Composable
fun rememberTextSizeState(textSizeManager: TextSizeManager): TextSizeState {
    val textScale by textSizeManager.textScale.collectAsState(initial = TextSizeScale.NORMAL.scale)
    val scope = rememberCoroutineScope()

    return TextSizeState(
        textScale = textScale,
        textSizeScale = TextSizeScale.fromScale(textScale),
        setTextScale = { scale ->
            scope.launch {
                textSizeManager.setTextScale(scale)
            }
        },
        increaseTextSize = {
            scope.launch {
                val current = TextSizeScale.fromScale(textScale)
                textSizeManager.setTextScale(TextSizeScale.next(current).scale)
            }
        },
        decreaseTextSize = {
            scope.launch {
                val current = TextSizeScale.fromScale(textScale)
                textSizeManager.setTextScale(TextSizeScale.previous(current).scale)
            }
        }
    )
}

data class TextSizeState(
    val textScale: Float,
    val textSizeScale: TextSizeScale,
    val setTextScale: (Float) -> Unit,
    val increaseTextSize: () -> Unit,
    val decreaseTextSize: () -> Unit
)
