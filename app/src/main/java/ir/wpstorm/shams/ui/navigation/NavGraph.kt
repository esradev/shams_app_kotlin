package ir.wpstorm.shams.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import ir.wpstorm.shams.ShamsApplication
import ir.wpstorm.shams.ui.screens.categories.CategoryScreen
import ir.wpstorm.shams.ui.screens.lessons.LessonScreen
import ir.wpstorm.shams.ui.screens.lessons.LessonsListScreen
import ir.wpstorm.shams.ui.screens.search.SearchScreen
import ir.wpstorm.shams.ui.screens.settings.SettingsScreen
import ir.wpstorm.shams.viewmodel.GlobalAudioPlayerViewModel
import ir.wpstorm.shams.viewmodel.SettingsViewModel
import ir.wpstorm.shams.viewmodel.SettingsViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    globalAudioPlayerViewModel: GlobalAudioPlayerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "categories",
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Categories screen
        composable("categories") {
            CategoryScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate("lessons/$categoryId")
                }
            )
        }

        // Lessons list screen
        composable(
            route = "lessons/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) { entry ->
            val categoryId = entry.arguments?.getInt("categoryId") ?: 0

            LessonsListScreen(
                categoryId = categoryId,
                onLessonClick = { lessonId ->
                    navController.navigate("lesson/$lessonId")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Single lesson screen
        composable(
            route = "lesson/{lessonId}?query={query}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.IntType },
                navArgument("query") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            val lessonId = entry.arguments?.getInt("lessonId") ?: 0
            val query = entry.arguments?.getString("query") ?: ""

            LessonScreen(
                lessonId = lessonId,
                searchQuery = query,
                globalAudioPlayerViewModel = globalAudioPlayerViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Search screen
        composable("search") {
            SearchScreen(
                onLessonClick = { lessonId, query ->
                    navController.navigate("lesson/$lessonId?query=$query")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Settings screen
        composable("settings") {
            val context = LocalContext.current
            val application = context.applicationContext as ShamsApplication
            val repository = application.downloadedAudioRepository
            val factory = SettingsViewModelFactory(repository)
            val viewModel: SettingsViewModel = viewModel(factory = factory)

            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel,
                onNavigateToLesson = { lessonId ->
                    navController.navigate("lesson/$lessonId")
                }
            )
        }
    }
}
