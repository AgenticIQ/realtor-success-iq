package com.realtorsuccessiq.ui.screens.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realtorsuccessiq.data.model.ActivityType
import com.realtorsuccessiq.util.Suggestion
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Streak banner
        item {
            if (uiState.leadGenStreak > 0 || uiState.callStreak > 0) {
                StreakBanner(
                    leadGenStreak = uiState.leadGenStreak,
                    callStreak = uiState.callStreak
                )
            }
        }
        
        // Lead Gen Timer
        item {
            LeadGenTimerCard(
                elapsedSeconds = uiState.leadGenElapsedSeconds,
                targetMinutes = uiState.targetLeadGenMinutes,
                isRunning = uiState.isTimerRunning,
                onStart = { viewModel.startTimer() },
                onPause = { viewModel.pauseTimer() },
                onStop = { viewModel.stopTimer() }
            )
        }
        
        // Progress Rings
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressRing(
                    label = "Calls",
                    current = uiState.dailyCalls,
                    target = uiState.targetCalls,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ProgressRing(
                    label = "Texts",
                    current = uiState.dailyTexts,
                    target = uiState.targetTexts,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ProgressRing(
                    label = "Asks",
                    current = uiState.dailyAsks,
                    target = uiState.targetAsks,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Next Best Actions
        item {
            Text(
                text = "Next Best Actions",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        items(uiState.suggestions) { suggestion ->
            SuggestionCard(
                suggestion = suggestion,
                onCall = { viewModel.logCall(suggestion.contact.id) },
                onText = { viewModel.logText(suggestion.contact.id) },
                onLogConversation = { viewModel.logConversation(suggestion.contact.id) }
            )
        }
    }
}

@Composable
fun StreakBanner(leadGenStreak: Int, callStreak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (leadGenStreak > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔥", style = MaterialTheme.typography.headlineMedium)
                    Text("$leadGenStreak day", style = MaterialTheme.typography.bodyMedium)
                    Text("Lead Gen", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (callStreak > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📞", style = MaterialTheme.typography.headlineMedium)
                    Text("$callStreak day", style = MaterialTheme.typography.bodyMedium)
                    Text("Call", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun LeadGenTimerCard(
    elapsedSeconds: Long,
    targetMinutes: Int,
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lead Gen Timer",
                style = MaterialTheme.typography.titleMedium
            )
            
            val minutes = TimeUnit.SECONDS.toMinutes(elapsedSeconds)
            val seconds = elapsedSeconds % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.headlineLarge
            )
            
            val progress = if (targetMinutes > 0) {
                (minutes.toFloat() / targetMinutes).coerceIn(0f, 1f)
            } else 0f
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            
            Text(
                text = "$minutes / $targetMinutes minutes",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isRunning) {
                    Button(onClick = onStart) {
                        Text("Start")
                    }
                } else {
                    OutlinedButton(onClick = onPause) {
                        Text("Pause")
                    }
                }
                OutlinedButton(onClick = onStop) {
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
fun ProgressRing(
    label: String,
    current: Int,
    target: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$current / $target",
                style = MaterialTheme.typography.headlineSmall
            )
            val progress = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
fun SuggestionCard(
    suggestion: Suggestion,
    onCall: () -> Unit,
    onText: () -> Unit,
    onLogConversation: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Show detail */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = suggestion.contact.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = suggestion.reason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onCall) {
                    Text("Call")
                }
                TextButton(onClick = onText) {
                    Text("Text")
                }
                TextButton(onClick = onLogConversation) {
                    Text("Log")
                }
            }
        }
    }
}

