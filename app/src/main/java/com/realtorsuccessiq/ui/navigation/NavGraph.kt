package com.realtorsuccessiq.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.realtorsuccessiq.ui.screens.leads.LeadsScreen
import com.realtorsuccessiq.ui.screens.review.ReviewScreen
import com.realtorsuccessiq.ui.screens.score.ScoreScreen
import com.realtorsuccessiq.ui.screens.settings.SettingsScreen
import com.realtorsuccessiq.ui.screens.today.TodayScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Today.route
    ) {
        composable(Screen.Today.route) {
            TodayScreen()
        }
        composable(Screen.Leads.route) {
            LeadsScreen()
        }
        composable(Screen.Score.route) {
            ScoreScreen()
        }
        composable(Screen.Review.route) {
            ReviewScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}

sealed class Screen(val route: String) {
    object Today : Screen("today")
    object Leads : Screen("leads")
    object Score : Screen("score")
    object Review : Screen("review")
    object Settings : Screen("settings")
}

