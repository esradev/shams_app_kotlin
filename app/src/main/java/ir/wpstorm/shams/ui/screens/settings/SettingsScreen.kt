package ir.wpstorm.shams.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SettingSwitch(
                title = "Offline mode",
                description = "Use downloaded lessons only",
                checked = offlineMode,
                onCheckedChange = { offlineMode = it }
            )

            SettingSwitch(
                title = "Auto download audio",
                description = "Download lesson audio automatically",
                checked = autoDownload,
                onCheckedChange = { autoDownload = it }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Shams al-Ma'arif\nVersion 1.0.0",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SettingSwitch(
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

