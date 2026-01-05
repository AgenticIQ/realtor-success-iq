package com.realtorsuccessiq.ui.admin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPrizesScreen(
    brokerageId: String,
    viewModel: AdminPrizesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(brokerageId) {
        viewModel.loadPrizes(brokerageId)
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Prize")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Prizes",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(uiState.prizes) { prize ->
                PrizeCard(
                    prize = prize,
                    onEdit = { /* Edit prize */ */ },
                    onDeactivate = { viewModel.deactivatePrize(prize.id) }
                )
            }
        }
    }
    
    if (showAddDialog) {
        AddPrizeDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { prize ->
                viewModel.createPrize(brokerageId, prize)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PrizeCard(
    prize: com.realtorsuccessiq.data.model.Prize,
    onEdit: () -> Unit,
    onDeactivate: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = prize.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = prize.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${prize.period.name} • ${prize.metric.name.replace("_", " ")}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (prize.isActive) {
                    TextButton(onClick = onDeactivate) {
                        Text("Deactivate")
                    }
                }
            }
        }
    }
}

@Composable
fun AddPrizeDialog(
    onDismiss: () -> Unit,
    onAdd: (com.realtorsuccessiq.data.model.Prize) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prizeType by remember { mutableStateOf(com.realtorsuccessiq.data.model.PrizeType.GIFT_CARD) }
    var metric by remember { mutableStateOf(com.realtorsuccessiq.data.model.LeaderboardMetric.TOTAL_CALLS) }
    var period by remember { mutableStateOf(com.realtorsuccessiq.data.model.LeaderboardPeriod.WEEKLY) }
    var rankThreshold by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Prize") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Prize Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Prize Value (e.g., $100 gift card)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rankThreshold,
                    onValueChange = { rankThreshold = it },
                    label = { Text("Top N (leave blank for top 10%)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val prize = com.realtorsuccessiq.data.model.Prize(
                        id = java.util.UUID.randomUUID().toString(),
                        brokerageId = "",
                        name = name,
                        description = description,
                        prizeType = prizeType,
                        metric = metric,
                        period = period,
                        rankThreshold = rankThreshold.toIntOrNull(),
                        value = value.ifBlank { null },
                        startDate = System.currentTimeMillis(),
                        endDate = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)
                    )
                    onAdd(prize)
                },
                enabled = name.isNotBlank() && description.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

