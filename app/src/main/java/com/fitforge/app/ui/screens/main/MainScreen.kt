package com.fitforge.app.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.fitforge.app.ui.screens.home.HomeScreen
import com.fitforge.app.ui.screens.nutrition.NutritionScreen
import com.fitforge.app.ui.screens.profile.ProfileScreen
import com.fitforge.app.ui.screens.progress.ProgressScreen
import com.fitforge.app.ui.screens.workout.WorkoutScreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("tab_home", "Home", Icons.Default.Home)
    object Workout : BottomNavItem("tab_workout", "Workout", Icons.Default.FitnessCenter)
    object Nutrition : BottomNavItem("tab_nutrition", "Nutrition", Icons.Default.Restaurant)
    object Progress : BottomNavItem("tab_progress", "Progress", Icons.AutoMirrored.Filled.ShowChart)
    object Profile : BottomNavItem("tab_profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainScreen(
    onLogoutClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Workout,
        BottomNavItem.Nutrition,
        BottomNavItem.Progress,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = selectedTab == item,
                        onClick = { selectedTab = item },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                is BottomNavItem.Home -> HomeScreen(
                    onNavigateToWorkout = { selectedTab = BottomNavItem.Workout },
                    onNavigateToNutrition = { selectedTab = BottomNavItem.Nutrition },
                    onNavigateToProgress = { selectedTab = BottomNavItem.Progress }
                )
                is BottomNavItem.Workout -> WorkoutScreen()
                is BottomNavItem.Nutrition -> NutritionScreen()
                is BottomNavItem.Progress -> ProgressScreen()
                is BottomNavItem.Profile -> ProfileScreen(onLogoutClick = onLogoutClick)
            }
        }
    }
}
