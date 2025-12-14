package ir.wpstorm.shams.util

import android.text.Html
import android.text.Spanned
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun HtmlRenderer(html: String) {
    val cleanHtml = html.take(1000) // Limit length to prevent memory issues
    val spanned: Spanned = try {
        Html.fromHtml(cleanHtml, Html.FROM_HTML_MODE_LEGACY)
    } catch (e: Exception) {
        // Fallback to plain text if HTML parsing fails
        Html.fromHtml("", Html.FROM_HTML_MODE_LEGACY)
    }

    BasicText(
        text = spanned.toString(),
        style = TextStyle(fontSize = 18.sp)
    )
}
