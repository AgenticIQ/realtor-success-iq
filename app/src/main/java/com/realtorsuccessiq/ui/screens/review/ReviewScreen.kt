package com.realtorsuccessiq.ui.screens.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel = hiltViewModel()
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Weekly Summary",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.summary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = uiState.whatWorked,
                onValueChange = { viewModel.updateWhatWorked(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("What worked?") },
                minLines = 3
            )
        }

        item {
            OutlinedTextField(
                value = uiState.whatDidntWork,
                onValueChange = { viewModel.updateWhatDidntWork(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("What didn't work?") },
                minLines = 3
            )
        }

        item {
            OutlinedTextField(
                value = uiState.nextWeekFocus,
                onValueChange = { viewModel.updateNextWeekFocus(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("What's the ONE focus next week?") },
                minLines = 3
            )
        }

        item {
            Button(
                onClick = { viewModel.saveReview() },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text("Save Review", maxLines = 2)
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

