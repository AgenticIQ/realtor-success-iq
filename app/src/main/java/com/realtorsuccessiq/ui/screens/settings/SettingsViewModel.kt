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
    private val tagCatalogInfoFlow = MutableStateFlow("Tag source: contacts (fallback)")

    private val baseUiState: Flow<SettingsUiState> = combine(
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
            availableTagsCount = availableTags.size,
            availableStagesCount = availableStages.size,
            contactsCount = contacts.size,
            // Filled in by a second combine() to avoid 6-flow combine overload issues.
            tagCatalogInfo = "",
            lastSyncAt = settings?.lastSyncAt,
            syncStatus = syncStatus
        )
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        baseUiState,
        tagCatalogInfoFlow
    ) { base, tagInfo ->
        base.copy(tagCatalogInfo = tagInfo)
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
                        tagCatalogInfoFlow.value = "Tag source: fetching FUB catalog…"
                        try {
                            val tags = crmRepository.fetchAllTags().sorted()
                            crmTagsFlow.value = tags
                            tagCatalogInfoFlow.value = if (tags.isNotEmpty()) {
                                "Tag source: FUB catalog (${tags.size})"
                            } else {
                                "Tag source: contacts (FUB catalog returned 0)"
                            }
                        } catch (e: Exception) {
                            crmTagsFlow.value = emptyList()
                            tagCatalogInfoFlow.value =
                                "Tag source: contacts (FUB tags fetch failed: ${e.message ?: e::class.java.simpleName})"
                        }
                        // stages endpoint is optional; we keep contact-derived stages unless implemented
                        crmStagesFlow.value = emptyList()
                    } else {
                        crmTagsFlow.value = emptyList()
                        crmStagesFlow.value = emptyList()
                        tagCatalogInfoFlow.value = "Tag source: contacts (no CRM)"
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

            // Refresh tag catalog on demand (so you don't have to re-enter the API key).
            if (settings.crmProvider == "followupboss" && !settings.crmApiKey.isNullOrBlank()) {
                tagCatalogInfoFlow.value = "Tag source: fetching FUB catalog…"
                try {
                    val tags = crmRepository.fetchAllTags().sorted()
                    crmTagsFlow.value = tags
                    tagCatalogInfoFlow.value = if (tags.isNotEmpty()) {
                        "Tag source: FUB catalog (${tags.size})"
                    } else {
                        "Tag source: contacts (FUB catalog returned 0)"
                    }
                } catch (e: Exception) {
                    crmTagsFlow.value = emptyList()
                    tagCatalogInfoFlow.value =
                        "Tag source: contacts (FUB tags fetch failed: ${e.message ?: e::class.java.simpleName})"
                }
            }

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
    val availableTagsCount: Int = 0,
    val availableStagesCount: Int = 0,
    val contactsCount: Int = 0,
    val tagCatalogInfo: String = "Tag source: contacts (fallback)",
    val lastSyncAt: Long? = null,
    val syncStatus: com.realtorsuccessiq.data.repository.SyncStatus = com.realtorsuccessiq.data.repository.SyncStatus.Disconnected
)

