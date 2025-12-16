package ir.wpstorm.shams.ui.components

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import ir.wpstorm.shams.ui.theme.Emerald100
import ir.wpstorm.shams.ui.theme.Emerald700

@Composable
fun HighlightableHtmlRenderer(
    modifier: Modifier = Modifier,
    html: String,
    searchQuery: String = "",
    onMatchesFound: (Int) -> Unit = {},
    currentMatchIndex: Int = 0
) {
    // Get the current text size from MaterialTheme
    val bodyLargeFontSize = MaterialTheme.typography.bodyLarge.fontSize
    val density = LocalDensity.current
    val textSizePx = with(density) { bodyLargeFontSize.toPx() }

    // Convert HTML to plain text for searching
    val plainText = remember(html) {
        HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    }

    // Find all matches
    val matches = remember(plainText, searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
            regex.findAll(plainText).map { it.range }.toList()
        }
    }

    LaunchedEffect(matches.size) {
        onMatchesFound(matches.size)
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                textDirection = android.view.View.TEXT_DIRECTION_RTL
                textAlignment = android.view.View.TEXT_ALIGNMENT_TEXT_START
                setPadding(0, 0, 0, 0)
                setLineSpacing(8f, 1.2f)
                @Suppress("DEPRECATION")
                setTextColor(android.graphics.Color.parseColor("#374151"))
            }
        },
        update = { view ->
            // Update text size when theme changes
            view.textSize = textSizePx / view.resources.displayMetrics.scaledDensity

            val spannedText = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)

            if (searchQuery.isNotBlank() && matches.isNotEmpty()) {
                val spannableBuilder = SpannableStringBuilder(spannedText)
                val highlightColor = Emerald100.toArgb()
                val currentHighlightColor = Emerald700.toArgb()

                // Apply highlights to all matches
                matches.forEachIndexed { index, range ->
                    val color = if (index == currentMatchIndex) {
                        currentHighlightColor
                    } else {
                        highlightColor
                    }

                    if (range.first < spannableBuilder.length && range.last < spannableBuilder.length) {
                        spannableBuilder.setSpan(
                            BackgroundColorSpan(color),
                            range.first,
                            range.last + 1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                view.text = spannableBuilder

                // Scroll to current match
                if (matches.isNotEmpty() && currentMatchIndex < matches.size) {
                    view.post {
                        val layout = view.layout
                        if (layout != null) {
                            val currentMatch = matches[currentMatchIndex]
                            val line = layout.getLineForOffset(currentMatch.first)
                            val y = layout.getLineTop(line)
                            view.scrollTo(0, y)
                        }
                    }
                }
            } else {
                view.text = spannedText
            }
        }
    )
}
