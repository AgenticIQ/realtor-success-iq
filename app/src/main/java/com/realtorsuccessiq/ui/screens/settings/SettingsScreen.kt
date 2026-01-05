package com.realtorsuccessiq.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
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

