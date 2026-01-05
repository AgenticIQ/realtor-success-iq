package com.realtorsuccessiq.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.crm.CrmConnector
import com.realtorsuccessiq.data.crm.DemoConnector
import com.realtorsuccessiq.data.crm.FollowUpBossConnector
import com.realtorsuccessiq.data.crm.SalesforceConnector
import com.realtorsuccessiq.data.model.UserSettings
import com.realtorsuccessiq.data.repository.CrmRepository
import com.realtorsuccessiq.data.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val crmRepository: CrmRepository
) : ViewModel() {
    
    val uiState: StateFlow<SettingsUiState> = localRepository.getSettings()
        .map { settings ->
            SettingsUiState(
                demoMode = settings?.demoMode ?: true,
                crmProvider = settings?.crmProvider ?: "demo",
                crmApiKey = settings?.crmApiKey,
                themePreset = settings?.themePreset ?: "success_minimal",
                privacyMode = settings?.privacyMode ?: false,
                biometricLock = settings?.biometricLockEnabled ?: false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )
    
    init {
        viewModelScope.launch {
            uiState.collect { state ->
                // Update CRM connector when provider changes
                val connector: CrmConnector? = when (state.crmProvider) {
                    "demo" -> DemoConnector()
                    "followupboss" -> {
                        if (!state.crmApiKey.isNullOrBlank()) {
                            FollowUpBossConnector(state.crmApiKey)
                        } else null
                    }
                    "salesforce" -> SalesforceConnector()
                    else -> null
                }
                crmRepository.setConnector(connector)
            }
        }
    }
    
    fun setDemoMode(enabled: Boolean) {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            localRepository.saveSettings(settings.copy(demoMode = enabled))
        }
    }
    
    fun setCrmProvider(provider: String) {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            localRepository.saveSettings(settings.copy(crmProvider = provider))
        }
    }
    
    fun setCrmApiKey(key: String) {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            localRepository.saveSettings(settings.copy(crmApiKey = key))
        }
    }
    
    fun setThemePreset(preset: String) {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            localRepository.saveSettings(settings.copy(themePreset = preset))
        }
    }
    
    fun setPrivacyMode(enabled: Boolean) {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            localRepository.saveSettings(settings.copy(privacyMode = enabled))
        }
    }
    
    fun setBiometricLock(enabled: Boolean) {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            localRepository.saveSettings(settings.copy(biometricLockEnabled = enabled))
        }
    }
}

data class SettingsUiState(
    val demoMode: Boolean = true,
    val crmProvider: String = "demo",
    val crmApiKey: String? = null,
    val themePreset: String = "success_minimal",
    val privacyMode: Boolean = false,
    val biometricLock: Boolean = false
)

