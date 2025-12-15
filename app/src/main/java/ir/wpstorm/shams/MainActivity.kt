package ir.wpstorm.shams

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ir.wpstorm.shams.ui.components.MainScaffold
import ir.wpstorm.shams.ui.navigation.NavGraph
import ir.wpstorm.shams.ui.theme.ShamsAlMaarifTheme
import ir.wpstorm.shams.ui.theme.ThemeManager
import ir.wpstorm.shams.ui.theme.rememberThemeState
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModel
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val themeManager = remember { ThemeManager(context) }
            val themeState = rememberThemeState(themeManager)

            ShamsAlMaarifTheme(darkTheme = themeState.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Create single shared ViewModel instance at activity level
                    val application = context.applicationContext as ShamsApplication
                    val globalAudioPlayerViewModel: GlobalAudioPlayerViewModel = viewModel(
                        factory = GlobalAudioPlayerViewModelFactory(application.globalAudioPlayer)
                    )

                    MainScaffold(
                        navController = navController,
                        themeState = themeState,
                        globalAudioPlayerViewModel = globalAudioPlayerViewModel,
                        onNavigateToLesson = { lessonId ->
                            navController.navigate("lesson/$lessonId")
                        }
                    ) { paddingValues ->
                        NavGraph(
                            navController = navController,
                            paddingValues = paddingValues,
                            globalAudioPlayerViewModel = globalAudioPlayerViewModel
                        )
                    }
                }
            }
        }
    }
}
