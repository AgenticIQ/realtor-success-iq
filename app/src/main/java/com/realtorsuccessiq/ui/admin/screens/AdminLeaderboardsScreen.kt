package com.realtorsuccessiq.ui.admin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realtorsuccessiq.data.model.LeaderboardMetric
import com.realtorsuccessiq.data.model.LeaderboardPeriod

@Composable
fun AdminLeaderboardsScreen(
    brokerageId: String,
    viewModel: AdminLeaderboardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(brokerageId) {
        viewModel.loadLeaderboard(brokerageId)
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Leaderboards",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedPeriod == LeaderboardPeriod.WEEKLY,
                    onClick = { viewModel.selectPeriod(LeaderboardPeriod.WEEKLY) },
                    label = { Text("Weekly") }
                )
                FilterChip(
                    selected = uiState.selectedPeriod == LeaderboardPeriod.MONTHLY,
                    onClick = { viewModel.selectPeriod(LeaderboardPeriod.MONTHLY) },
                    label = { Text("Monthly") }
                )
                FilterChip(
                    selected = uiState.selectedPeriod == LeaderboardPeriod.ANNUAL,
                    onClick = { viewModel.selectPeriod(LeaderboardPeriod.ANNUAL) },
                    label = { Text("Annual") }
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedMetric == LeaderboardMetric.TOTAL_CALLS,
                    onClick = { viewModel.selectMetric(LeaderboardMetric.TOTAL_CALLS) },
                    label = { Text("Calls") }
                )
                FilterChip(
                    selected = uiState.selectedMetric == LeaderboardMetric.APPOINTMENTS_SET,
                    onClick = { viewModel.selectMetric(LeaderboardMetric.APPOINTMENTS_SET) },
                    label = { Text("Appointments") }
                )
                FilterChip(
                    selected = uiState.selectedMetric == LeaderboardMetric.LISTINGS_SIGNED,
                    onClick = { viewModel.selectMetric(LeaderboardMetric.LISTINGS_SIGNED) },
                    label = { Text("Listings") }
                )
            }
        }
        
        item {
            Button(onClick = { viewModel.refreshLeaderboard(brokerageId) }) {
                Text("Refresh Leaderboard")
            }
        }
        
        items(uiState.entries) { entry ->
            LeaderboardEntryCard(entry = entry, agentName = uiState.agentNames[entry.agentId] ?: "Unknown")
        }
    }
}

@Composable
fun LeaderboardEntryCard(
    entry: com.realtorsuccessiq.data.model.LeaderboardEntry,
    agentName: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "#${entry.rank}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = agentName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = entry.metric.name.replace("_", " "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "${entry.value}",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
