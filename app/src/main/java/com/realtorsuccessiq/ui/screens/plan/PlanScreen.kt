package com.realtorsuccessiq.ui.screens.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    viewModel: PlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Success Plan (90-Day)", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Default plan is listings-first. You can customize targets below.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Daily Non‑Negotiables", style = MaterialTheme.typography.titleMedium)

                    Text("Lead gen minutes: ${uiState.dailyLeadGenMinutes}")
                    Slider(
                        value = uiState.dailyLeadGenMinutes.toFloat(),
                        onValueChange = { viewModel.setDailyLeadGenMinutes(it.toInt()) },
                        valueRange = 30f..240f,
                        steps = 6
                    )

                    Text("Calls/day: ${uiState.dailyCallsTarget}")
                    Slider(
                        value = uiState.dailyCallsTarget.toFloat(),
                        onValueChange = { viewModel.setDailyCallsTarget(it.toInt()) },
                        valueRange = 0f..50f,
                        steps = 9
                    )

                    Text("Texts/day: ${uiState.dailyTextsTarget}")
                    Slider(
                        value = uiState.dailyTextsTarget.toFloat(),
                        onValueChange = { viewModel.setDailyTextsTarget(it.toInt()) },
                        valueRange = 0f..50f,
                        steps = 9
                    )

                    Text("Appointment asks/day: ${uiState.dailyAppointmentAsksTarget}")
                    Slider(
                        value = uiState.dailyAppointmentAsksTarget.toFloat(),
                        onValueChange = { viewModel.setDailyAppointmentAsksTarget(it.toInt()) },
                        valueRange = 0f..10f,
                        steps = 9
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Conversation rule", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Choose whether texts count toward “conversations” for your scorecard.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    RowSwitch(
                        title = "Texts count as conversations",
                        checked = uiState.textsCountAsConversations,
                        onCheckedChange = { viewModel.setTextsCountAsConversations(it) }
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("User Logo (simple mark)", style = MaterialTheme.typography.titleMedium)
                    var logo by remember(uiState.userLogoText) { mutableStateOf(uiState.userLogoText) }
                    OutlinedTextField(
                        value = logo,
                        onValueChange = {
                            logo = it
                            viewModel.setUserLogoText(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Logo text (e.g., JH or Brokerage)") },
                        singleLine = true
                    )
                    Text(
                        "This is internal and used in the UI header. (No photo required.)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RowSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

