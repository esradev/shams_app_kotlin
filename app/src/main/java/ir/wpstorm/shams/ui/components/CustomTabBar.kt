package ir.wpstorm.shams.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ir.wpstorm.shams.ui.theme.Emerald700

@Composable
fun CustomTabBar(
    navController: NavController,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination?.route

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        NavigationBar(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            defaultNavigationItems.forEach { item ->
                // Skip items that don't exist in current navigation
                if (item.route == "courses" || item.route == "my-progress") {
                    return@forEach
                }

                val isSelected = when (item.route) {
                    "categories" -> currentDestination == "categories" || currentDestination?.startsWith("lessons/") == true
                    "search" -> currentDestination == "search"
                    "settings" -> currentDestination == "settings"
                    else -> false
                }

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        when (item.route) {
                            "categories" -> {
                                navController.navigate("categories") {
                                    popUpTo("categories") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                            "search" -> {
                                navController.navigate("search") {
                                    launchSingleTop = true
                                }
                            }
                            "settings" -> {
                                navController.navigate("settings") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald700,
                        selectedTextColor = Emerald700,
                        indicatorColor = Emerald700.copy(alpha = 0.12f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
