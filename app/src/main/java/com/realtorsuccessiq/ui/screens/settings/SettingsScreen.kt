package com.realtorsuccessiq.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realtorsuccessiq.BuildConfig
import com.realtorsuccessiq.updates.NightlyUpdateManager
import com.realtorsuccessiq.data.repository.SyncStatus

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showTagsDialog by remember { mutableStateOf(false) }
    var showStagesDialog by remember { mutableStateOf(false) }
    var updateStatus by remember { mutableStateOf<String?>(null) }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        item {
            SwitchSetting(
                title = "Demo Mode",
                checked = uiState.demoMode,
                onCheckedChange = { viewModel.setDemoMode(it) }
            )
        }
        
        item {
            Text(
                text = "CRM Provider",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            CRMProviderSelector(
                selectedProvider = uiState.crmProvider,
                onProviderSelected = { viewModel.setCrmProvider(it) }
            )
        }
        
        item {
            if (uiState.crmProvider == "followupboss") {
                OutlinedTextField(
                    value = uiState.crmApiKey ?: "",
                    onValueChange = { viewModel.setCrmApiKey(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Follow Up Boss API Key") },
                    singleLine = true
                )
                Text(
                    text = "Note: “Demo Mode” is only for sign-in. You can still sync real contacts from Follow Up Boss here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            val isSyncing = uiState.syncStatus is SyncStatus.Syncing
            Button(
                onClick = { viewModel.syncNow() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSyncing
            ) {
                Text(if (isSyncing) "Syncing…" else "Sync Now")
            }
        }

        item {
            val statusText = when (uiState.syncStatus) {
                is SyncStatus.Connected -> "Connected"
                is SyncStatus.Syncing -> "Syncing…"
                is SyncStatus.RateLimited -> "Rate limited"
                is SyncStatus.Error -> "Error: ${(uiState.syncStatus as SyncStatus.Error).message}"
                is SyncStatus.Disconnected -> "Disconnected"
            }
            Text(text = "Status: $statusText", style = MaterialTheme.typography.bodyMedium)
        }

        item {
            Text(
                text = "CRM Focus Filters",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showTagsDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (uiState.focusTags.isEmpty()) "Select Focus Tags"
                        else "Focus Tags: ${uiState.focusTags.joinToString(", ")}"
                    )
                }
                OutlinedButton(
                    onClick = { showStagesDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (uiState.focusStages.isEmpty()) "Select Focus Stages"
                        else "Focus Stages: ${uiState.focusStages.joinToString(", ")}"
                    )
                }
                Text(
                    text = "Tip: If you select tags/stages here, Leads + Today suggestions will only show matching contacts.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            Text(
                text = "Updates (GitHub)",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Current app: v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    style = MaterialTheme.typography.bodySmall
                )
                Button(
                    onClick = {
                        updateStatus = "Starting download…"
                        when (val result = NightlyUpdateManager.startDownloadAndInstall(context)) {
                            is NightlyUpdateManager.Result.Started -> {
                                updateStatus = "Downloading… you’ll get an install prompt when it finishes."
                            }
                            is NightlyUpdateManager.Result.NeedsUnknownSourcesPermission -> {
                                updateStatus = "Enable “Install unknown apps” for RealtorSuccessIQ, then tap again."
                            }
                            is NightlyUpdateManager.Result.Error -> {
                                updateStatus = "Update failed: ${result.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Download & Install Latest (Nightly)") }

                Text(
                    text = "Android won’t allow silent auto-updates for APK installs. This button downloads the newest build and opens the installer prompt.",
                    style = MaterialTheme.typography.bodySmall
                )

                if (updateStatus != null) {
                    Text(
                        text = updateStatus!!,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            val lastSync = uiState.lastSyncAt
            if (lastSync != null) {
                Text(
                    text = "Last sync: ${java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault()).format(java.util.Date(lastSync))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Last sync: never",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        item {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            ThemeSelector(
                selectedPreset = uiState.themePreset,
                onPresetSelected = { viewModel.setThemePreset(it) }
            )
        }
        
        item {
            SwitchSetting(
                title = "Privacy Mode",
                checked = uiState.privacyMode,
                onCheckedChange = { viewModel.setPrivacyMode(it) }
            )
        }
        
        item {
            SwitchSetting(
                title = "Biometric Lock",
                checked = uiState.biometricLock,
                onCheckedChange = { viewModel.setBiometricLock(it) }
            )
        }
    }

    if (showTagsDialog) {
        MultiSelectDialog(
            title = "Focus Tags",
            options = uiState.availableTags,
            selected = uiState.focusTags,
            onDismiss = { showTagsDialog = false },
            onApply = { viewModel.setFocusTags(it); showTagsDialog = false }
        )
    }

    if (showStagesDialog) {
        MultiSelectDialog(
            title = "Focus Stages",
            options = uiState.availableStages,
            selected = uiState.focusStages,
            onDismiss = { showStagesDialog = false },
            onApply = { viewModel.setFocusStages(it); showStagesDialog = false }
        )
    }
}

@Composable
fun SwitchSetting(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun CRMProviderSelector(
    selectedProvider: String,
    onProviderSelected: (String) -> Unit
) {
    val providers = listOf("demo", "followupboss", "salesforce")
    
    providers.forEach { provider ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            RadioButton(
                selected = selectedProvider == provider,
                onClick = { onProviderSelected(provider) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = provider.replaceFirstChar { it.uppercaseChar() },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ThemeSelector(
    selectedPreset: String,
    onPresetSelected: (String) -> Unit
) {
    val presets = listOf("success_minimal", "trust_blue", "modern_charcoal", "coastal")
    
    presets.forEach { preset ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            RadioButton(
                selected = selectedPreset == preset,
                onClick = { onPresetSelected(preset) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = preset.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun MultiSelectDialog(
    title: String,
    options: List<String>,
    selected: List<String>,
    onDismiss: () -> Unit,
    onApply: (List<String>) -> Unit
) {
    var workingSelection by remember(selected, options) {
        mutableStateOf(selected.toSet())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            if (options.isEmpty()) {
                Text("No options yet. Tap “Sync Now” to pull contacts, then try again.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(options.size) { idx ->
                        val option = options[idx]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = workingSelection.contains(option),
                                onCheckedChange = { checked ->
                                    workingSelection = if (checked) {
                                        workingSelection + option
                                    } else {
                                        workingSelection - option
                                    }
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(workingSelection.toList().sorted()) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

