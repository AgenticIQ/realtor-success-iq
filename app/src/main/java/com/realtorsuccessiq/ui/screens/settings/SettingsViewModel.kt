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
    
    private val settingsFlow = localRepository.getSettings()
    private val contactsFlow = localRepository.getAllContacts()

    private val crmTagsFlow = MutableStateFlow<List<String>>(emptyList())
    private val crmStagesFlow = MutableStateFlow<List<String>>(emptyList())

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsFlow,
        contactsFlow,
        crmTagsFlow,
        crmStagesFlow,
        crmRepository.syncStatus
    ) { settings, contacts, crmTags, crmStages, syncStatus ->
        val focusTags = settings?.crmFocusTags
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()
        val focusStages = settings?.crmFocusStages
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()

        val contactTags = contacts
            .flatMap { it.getTagsList() }
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        val availableTags = (if (crmTags.isNotEmpty()) crmTags else contactTags)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        val contactStages = contacts
            .mapNotNull { it.stage?.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        val availableStages = (if (crmStages.isNotEmpty()) crmStages else contactStages)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        SettingsUiState(
            demoMode = settings?.demoMode ?: true,
            crmProvider = settings?.crmProvider ?: "demo",
            crmApiKey = settings?.crmApiKey,
            themePreset = settings?.themePreset ?: "success_minimal",
            privacyMode = settings?.privacyMode ?: false,
            biometricLock = settings?.biometricLockEnabled ?: false,
            focusTags = focusTags,
            focusStages = focusStages,
            availableTags = availableTags,
            availableStages = availableStages,
            lastSyncAt = settings?.lastSyncAt,
            syncStatus = syncStatus
        )
    }.stateIn(
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

        viewModelScope.launch {
            // Pull tag catalog when CRM credentials are present.
            settingsFlow
                .map { (it?.crmProvider ?: "demo") to (it?.crmApiKey ?: "") }
                .distinctUntilChanged()
                .collect { (provider, key) ->
                    if (provider == "followupboss" && key.isNotBlank()) {
                        crmTagsFlow.value = crmRepository.fetchAllTags().sorted()
                        // stages endpoint is optional; we keep contact-derived stages unless implemented
                        crmStagesFlow.value = emptyList()
                    } else {
                        crmTagsFlow.value = emptyList()
                        crmStagesFlow.value = emptyList()
                    }
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
            if (provider != "demo") {
                // When switching to a real CRM, remove seeded demo contacts to avoid mixing datasets.
                localRepository.deleteDemoContacts()
                localRepository.saveSettings(settings.copy(crmProvider = provider, demoMode = false))
            } else {
                localRepository.saveSettings(settings.copy(crmProvider = provider))
            }
        }
    }
    
    fun setCrmApiKey(key: String) {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            localRepository.saveSettings(settings.copy(crmApiKey = key))
        }
    }

    fun setFocusTags(tags: List<String>) {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            val value = tags.map { it.trim() }.filter { it.isNotBlank() }.distinct().joinToString(",")
            localRepository.saveSettings(settings.copy(crmFocusTags = value))
        }
    }

    fun setFocusStages(stages: List<String>) {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            val value = stages.map { it.trim() }.filter { it.isNotBlank() }.distinct().joinToString(",")
            localRepository.saveSettings(settings.copy(crmFocusStages = value))
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            val settings = localRepository.getSettingsSync() ?: UserSettings()
            if (settings.crmProvider != "demo") {
                // Ensure mock/demo contacts aren't mixed into real CRM testing.
                localRepository.deleteDemoContacts()
            }
            crmRepository.validateConnection()
            crmRepository.syncDownContacts()
            crmRepository.syncDownTasks()
            localRepository.saveSettings(settings.copy(lastSyncAt = System.currentTimeMillis()))
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
    val biometricLock: Boolean = false,
    val focusTags: List<String> = emptyList(),
    val focusStages: List<String> = emptyList(),
    val availableTags: List<String> = emptyList(),
    val availableStages: List<String> = emptyList(),
    val lastSyncAt: Long? = null,
    val syncStatus: com.realtorsuccessiq.data.repository.SyncStatus = com.realtorsuccessiq.data.repository.SyncStatus.Disconnected
)

