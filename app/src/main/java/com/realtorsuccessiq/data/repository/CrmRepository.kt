package com.realtorsuccessiq.data.repository

import com.realtorsuccessiq.data.crm.*
import com.realtorsuccessiq.data.model.Contact
import com.realtorsuccessiq.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CrmRepository(
    private val localRepository: LocalRepository
) {
    private var currentConnector: CrmConnector? = null
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Disconnected)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus
    
    fun setConnector(connector: CrmConnector?) {
        currentConnector = connector
        if (connector == null) {
            _syncStatus.value = SyncStatus.Disconnected
        }
    }

    private suspend fun ensureConnectorConfigured() {
        if (currentConnector != null) return
        val settings = localRepository.getSettingsSync()
        val connector: CrmConnector? = when (settings?.crmProvider ?: "demo") {
            "demo" -> DemoConnector()
            "followupboss" -> {
                val key = settings?.crmApiKey
                if (!key.isNullOrBlank()) FollowUpBossConnector(key) else null
            }
            "salesforce" -> SalesforceConnector()
            else -> null
        }
        setConnector(connector)
    }
    
    suspend fun validateConnection(): ConnectionStatus {
        ensureConnectorConfigured()
        val connector = currentConnector ?: return ConnectionStatus.Disconnected
        val status = connector.validateConnection()
        _syncStatus.value = when (status) {
            is ConnectionStatus.Connected -> SyncStatus.Connected
            is ConnectionStatus.Error -> SyncStatus.Error(status.message)
            else -> SyncStatus.Disconnected
        }
        return status
    }
    
    suspend fun syncDownContacts(): SyncResult {
        ensureConnectorConfigured()
        val connector = currentConnector ?: return SyncResult.Error("No CRM connector configured")
        _syncStatus.value = SyncStatus.Syncing
        
        return try {
            val result = connector.syncDownContacts()
            when (result) {
                is SyncResult.Success -> {
                    // Minimal offline-first sync: fetch mapped contacts from connector and upsert into Room.
                    // (For connectors that don't implement search, this will just insert nothing.)
                    val contacts = connector.searchContacts("")
                    if (contacts.isNotEmpty()) {
                        localRepository.insertContacts(contacts)
                    }
                    _syncStatus.value = SyncStatus.Connected
                }
                is SyncResult.RateLimited -> {
                    _syncStatus.value = SyncStatus.RateLimited
                }
                is SyncResult.Error -> {
                    _syncStatus.value = SyncStatus.Error(result.message)
                }
            }
            result
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error(e.message ?: "Sync failed")
            SyncResult.Error(e.message ?: "Sync failed")
        }
    }
    
    suspend fun syncDownTasks(): SyncResult {
        ensureConnectorConfigured()
        val connector = currentConnector ?: return SyncResult.Error("No CRM connector configured")
        _syncStatus.value = SyncStatus.Syncing
        
        return try {
            val result = connector.syncDownTasks()
            when (result) {
                is SyncResult.Success -> {
                    _syncStatus.value = SyncStatus.Connected
                }
                is SyncResult.RateLimited -> {
                    _syncStatus.value = SyncStatus.RateLimited
                }
                is SyncResult.Error -> {
                    _syncStatus.value = SyncStatus.Error(result.message)
                }
            }
            result
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error(e.message ?: "Sync failed")
            SyncResult.Error(e.message ?: "Sync failed")
        }
    }
    
    suspend fun pushCallLog(callLog: Map<String, Any>): PushResult {
        ensureConnectorConfigured()
        val connector = currentConnector ?: return PushResult.Error("No CRM connector configured")
        return connector.pushCallLog(callLog)
    }
    
    suspend fun pushNote(note: Map<String, Any>): PushResult {
        ensureConnectorConfigured()
        val connector = currentConnector ?: return PushResult.Error("No CRM connector configured")
        return connector.pushNote(note)
    }
    
    suspend fun pushTask(task: Map<String, Any>): PushResult {
        ensureConnectorConfigured()
        val connector = currentConnector ?: return PushResult.Error("No CRM connector configured")
        return connector.pushTask(task)
    }
    
    suspend fun searchContacts(query: String): List<Contact> {
        ensureConnectorConfigured()
        val connector = currentConnector ?: return emptyList()
        return connector.searchContacts(query)
    }
}

sealed class SyncStatus {
    data object Connected : SyncStatus()
    data object Syncing : SyncStatus()
    data object Disconnected : SyncStatus()
    data class Error(val message: String) : SyncStatus()
    data object RateLimited : SyncStatus()
}

