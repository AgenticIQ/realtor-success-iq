package com.realtorsuccessiq.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.realtorsuccessiq.BuildConfig
import com.realtorsuccessiq.ui.screens.leads.LeadsScreen
import com.realtorsuccessiq.ui.screens.plan.PlanScreen
import com.realtorsuccessiq.ui.screens.privacy.PrivacyPolicyScreen
import com.realtorsuccessiq.ui.screens.review.ReviewScreen
import com.realtorsuccessiq.ui.screens.score.ScoreScreen
import com.realtorsuccessiq.ui.screens.settings.SettingsScreen
import com.realtorsuccessiq.ui.screens.today.TodayScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getScreenTitle(currentDestination?.route)) },
                actions = {
                    // Sync status chip will go here
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val screens = buildList {
                    add(Screen.Today to Icons.Default.Today)
                    add(Screen.Leads to Icons.Default.Person)
                    add(Screen.Score to Icons.Default.Star)
                    if (BuildConfig.FLAVOR == "next") {
                        add(Screen.Plan to Icons.Default.List)
                    }
                    add(Screen.Review to Icons.Default.Assessment)
                    add(Screen.Settings to Icons.Default.Settings)
                }
                
                screens.forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen.route) },
                        label = { Text(screen.route.replaceFirstChar { it.uppercaseChar() }) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { navController.navigate(screen.route) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Today.route
            ) {
                composable(Screen.Today.route) { TodayScreen() }
                composable(Screen.Leads.route) { LeadsScreen() }
                composable(Screen.Score.route) { ScoreScreen() }
                if (BuildConfig.FLAVOR == "next") {
                    composable(Screen.Plan.route) { PlanScreen() }
                }
                composable(Screen.Review.route) { ReviewScreen() }
                composable(Screen.Settings.route) { SettingsScreen() }
                composable(Screen.Privacy.route) { PrivacyPolicyScreen() }
            }
        }
    }
}

private fun getScreenTitle(route: String?): String {
    return when (route) {
        Screen.Today.route -> "Today"
        Screen.Leads.route -> "Leads"
        Screen.Score.route -> "Score"
        Screen.Plan.route -> "Plan"
        Screen.Review.route -> "Review"
        Screen.Settings.route -> "Settings"
        Screen.Privacy.route -> "Privacy"
        else -> "Realtor Success IQ"
    }
}

