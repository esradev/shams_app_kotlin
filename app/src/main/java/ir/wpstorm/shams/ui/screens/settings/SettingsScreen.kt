package ir.wpstorm.shams.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    var offlineMode by remember { mutableStateOf(false) }
    var autoDownload by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "تنظیمات",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingSwitch(
                title = "حالت آفلاین",
                description = "فقط از دروس دانلود شده استفاده کن",
                checked = offlineMode,
                onCheckedChange = { offlineMode = it }
            )

            SettingSwitch(
                title = "دانلود خودکار صوت",
                description = "دانلود خودکار صوت درس‌ها",
                checked = autoDownload,
                onCheckedChange = { autoDownload = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "درباره برنامه",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "شمس المعارف\nنسخه 1.0.0",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

    }
}

