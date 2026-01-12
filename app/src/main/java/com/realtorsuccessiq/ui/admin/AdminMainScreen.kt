package com.realtorsuccessiq.ui.admin

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
import com.realtorsuccessiq.ui.admin.navigation.AdminNavGraph
import com.realtorsuccessiq.ui.admin.navigation.AdminScreen
import com.realtorsuccessiq.ui.admin.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(brokerageId: String, onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Broker Admin") },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val screens = listOf(
                    AdminScreen.Dashboard to Icons.Default.Dashboard,
                    AdminScreen.Agents to Icons.Default.People,
                    AdminScreen.Leaderboards to Icons.Default.EmojiEvents,
                    AdminScreen.Prizes to Icons.Default.CardGiftcard,
                    AdminScreen.Branding to Icons.Default.Palette
                )
                
                screens.forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen.route) },
                        label = { Text(getScreenTitle(screen)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { navController.navigate(screen.route) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AdminNavGraph(navController = navController, brokerageId = brokerageId)
        }
    }
}

private fun getScreenTitle(screen: AdminScreen): String {
    return when (screen) {
        is AdminScreen.Dashboard -> "Dashboard"
        is AdminScreen.Agents -> "Agents"
        is AdminScreen.Leaderboards -> "Leaderboards"
        is AdminScreen.Prizes -> "Prizes"
        is AdminScreen.Branding -> "Branding"
    }
}
