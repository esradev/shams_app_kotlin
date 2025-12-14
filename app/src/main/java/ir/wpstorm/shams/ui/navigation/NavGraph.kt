package ir.wpstorm.shams.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import ir.wpstorm.shams.ui.screens.categories.CategoryScreen
import ir.wpstorm.shams.ui.screens.lessons.LessonScreen
import ir.wpstorm.shams.ui.screens.lessons.LessonsListScreen
import ir.wpstorm.shams.ui.screens.search.SearchScreen
import ir.wpstorm.shams.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "categories"
    ) {
        // Categories screen
        composable("categories") {
            CategoryScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate("lessons/$categoryId")
                },
                onSearchClick = {
                    navController.navigate("search")
                },
                onSettingsClick = {
                    navController.navigate("settings")
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
            route = "lesson/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { entry ->
            val lessonId = entry.arguments?.getInt("lessonId") ?: 0

            LessonScreen(
                lessonId = lessonId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Search screen
        composable("search") {
            SearchScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLessonClick = { lessonId ->
                    navController.navigate("lesson/$lessonId")
                }
            )
        }

        // Settings screen
        composable("settings") {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
