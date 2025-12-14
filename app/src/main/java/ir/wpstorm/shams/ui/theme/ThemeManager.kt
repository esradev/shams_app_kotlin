package ir.wpstorm.shams.ui.theme

import android.content.Context
import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemeManager(private val context: Context) {
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDark
        }
    }
}

@Composable
fun rememberThemeState(themeManager: ThemeManager): ThemeState {
    val isDarkTheme by themeManager.isDarkTheme.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    return ThemeState(
        isDarkTheme = isDarkTheme,
        toggleTheme = {
            scope.launch {
                themeManager.setDarkTheme(!isDarkTheme)
            }
        }
    )
}

data class ThemeState(
    val isDarkTheme: Boolean,
    val toggleTheme: () -> Unit
)
