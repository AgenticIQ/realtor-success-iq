package com.realtorsuccessiq.ui.screens.score

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ScoreScreen(
    viewModel: ScoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Stars",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${uiState.totalStars}",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
        
        item {
            Text(
                text = "Streaks",
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Lead Gen", style = MaterialTheme.typography.bodyMedium)
                        Text("${uiState.leadGenStreak} days", style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Card(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Calls", style = MaterialTheme.typography.bodyMedium)
                        Text("${uiState.callStreak} days", style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
        
        item {
            Text(
                text = "Badges",
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        items(uiState.badges) { badge ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏆", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = badge.badgeType,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Unlocked ${formatDate(badge.unlockedAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
    return format.format(date)
}
