package ir.wpstorm.shams.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Emerald400,
    secondary = Emerald700,
    tertiary = Pink80,
    background = Gray900,
    surface = Gray800,
    surfaceVariant = Gray700,
    onPrimary = Gray900,
    onSecondary = Color.White,
    onTertiary = Gray900,
    onBackground = Color(0xFFE5E7EB), // Light gray for better contrast
    onSurface = Color(0xFFE5E7EB), // Light gray for better contrast
    onSurfaceVariant = Color(0xFFD1D5DB), // Lighter gray for secondary text
    outline = Gray600,
    outlineVariant = Gray500
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald700,
    secondary = Emerald400,
    tertiary = Pink40,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Gray50,
    onPrimary = Color.White,
    onSecondary = Gray900,
    onTertiary = Color.White,
    onBackground = Gray900,
    onSurface = Gray900,
    onSurfaceVariant = Gray600,
    outline = Gray400,
    outlineVariant = Gray200
)

@Composable
fun ShamsAlMaarifTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}