package com.realtorsuccessiq.data.crm

import com.realtorsuccessiq.data.model.Contact
import com.realtorsuccessiq.data.model.Task
import kotlinx.coroutines.delay

class SalesforceConnector : CrmConnector {
    // Stub implementation for MVP - returns mock data
    // Real OAuth2 implementation will be added in Phase 2
    
    override suspend fun validateConnection(): ConnectionStatus {
        delay(500)
        return ConnectionStatus.Error("Salesforce connector not yet implemented. Use Demo Mode or Follow Up Boss.")
    }
    
    override suspend fun syncDownContacts(cursor: String?): SyncResult {
        delay(500)
        return SyncResult.Error("Not implemented")
    }
    
    override suspend fun syncDownTasks(cursor: String?): SyncResult {
        delay(500)
        return SyncResult.Error("Not implemented")
    }
    
    override suspend fun pushCallLog(callLog: Map<String, Any>): PushResult {
        delay(300)
        return PushResult.Error("Not implemented")
    }
    
    override suspend fun pushNote(note: Map<String, Any>): PushResult {
        delay(300)
        return PushResult.Error("Not implemented")
    }
    
    override suspend fun pushTask(task: Map<String, Any>): PushResult {
        delay(300)
        return PushResult.Error("Not implemented")
    }
    
    override suspend fun searchContacts(query: String): List<Contact> {
        delay(200)
        return emptyList()
    }
    
    override suspend fun getContactById(id: String): Contact? {
        delay(100)
        return null
    }
}
