package com.realtorsuccessiq.ui.admin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.realtorsuccessiq.ui.admin.screens.*

@Composable
fun AdminNavGraph(navController: NavHostController, brokerageId: String) {
    NavHost(
        navController = navController,
        startDestination = AdminScreen.Dashboard.route
    ) {
        composable(AdminScreen.Dashboard.route) {
            AdminDashboardScreen(brokerageId = brokerageId)
        }
        composable(AdminScreen.Agents.route) {
            AdminAgentsScreen(brokerageId = brokerageId)
        }
        composable(AdminScreen.Leaderboards.route) {
            AdminLeaderboardsScreen(brokerageId = brokerageId)
        }
        composable(AdminScreen.Prizes.route) {
            AdminPrizesScreen(brokerageId = brokerageId)
        }
        composable(AdminScreen.Branding.route) {
            AdminBrandingScreen(brokerageId = brokerageId)
        }
    }
}

sealed class AdminScreen(val route: String) {
    object Dashboard : AdminScreen("admin_dashboard")
    object Agents : AdminScreen("admin_agents")
    object Leaderboards : AdminScreen("admin_leaderboards")
    object Prizes : AdminScreen("admin_prizes")
    object Branding : AdminScreen("admin_branding")
}
