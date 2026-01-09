package com.realtorsuccessiq.data.crm

import com.realtorsuccessiq.data.model.Contact
import com.realtorsuccessiq.data.model.Task

sealed class ConnectionStatus {
    object Connected : ConnectionStatus()
    object Disconnected : ConnectionStatus()
    data class Error(val message: String) : ConnectionStatus()
}

sealed class SyncResult {
    data class Success(val count: Int) : SyncResult()
    data class Error(val message: String) : SyncResult()
    object RateLimited : SyncResult()
}

sealed class PushResult {
    object Success : PushResult()
    data class Error(val message: String) : PushResult()
}

interface CrmConnector {
    suspend fun validateConnection(): ConnectionStatus
    suspend fun syncDownContacts(cursor: String? = null): SyncResult
    suspend fun syncDownTasks(cursor: String? = null): SyncResult
    suspend fun pushCallLog(callLog: Map<String, Any>): PushResult
    suspend fun pushNote(note: Map<String, Any>): PushResult
    suspend fun pushTask(task: Map<String, Any>): PushResult
    suspend fun searchContacts(query: String): List<Contact>
    suspend fun getContactById(id: String): Contact?

    /**
     * Optional: return the full CRM tag catalog (not just tags present on synced contacts).
     */
    suspend fun fetchAllTags(): List<String> = emptyList()

    /**
     * Optional: return the full CRM stage/pipeline catalog.
     */
    suspend fun fetchAllStages(): List<String> = emptyList()
}

