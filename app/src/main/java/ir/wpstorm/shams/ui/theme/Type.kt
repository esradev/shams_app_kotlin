package ir.wpstorm.shams.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Create typography with dynamic text scale
fun createTypography(textScale: Float = 1.0f) = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (57 * textScale).sp,
        lineHeight = (64 * textScale).sp,
        letterSpacing = (-0.25 * textScale).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (45 * textScale).sp,
        lineHeight = (52 * textScale).sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (36 * textScale).sp,
        lineHeight = (44 * textScale).sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (32 * textScale).sp,
        lineHeight = (40 * textScale).sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (28 * textScale).sp,
        lineHeight = (36 * textScale).sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (24 * textScale).sp,
        lineHeight = (32 * textScale).sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (22 * textScale).sp,
        lineHeight = (28 * textScale).sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = (16 * textScale).sp,
        lineHeight = (24 * textScale).sp,
        letterSpacing = (0.15 * textScale).sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = (14 * textScale).sp,
        lineHeight = (20 * textScale).sp,
        letterSpacing = (0.1 * textScale).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (16 * textScale).sp,
        lineHeight = (24 * textScale).sp,
        letterSpacing = (0.5 * textScale).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (14 * textScale).sp,
        lineHeight = (20 * textScale).sp,
        letterSpacing = (0.25 * textScale).sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = (12 * textScale).sp,
        lineHeight = (16 * textScale).sp,
        letterSpacing = (0.4 * textScale).sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = (14 * textScale).sp,
        lineHeight = (20 * textScale).sp,
        letterSpacing = (0.1 * textScale).sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = (12 * textScale).sp,
        lineHeight = (16 * textScale).sp,
        letterSpacing = (0.5 * textScale).sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = (11 * textScale).sp,
        lineHeight = (16 * textScale).sp,
        letterSpacing = (0.5 * textScale).sp
    )
)

// Default typography for backward compatibility
val Typography = createTypography()
