package com.realtorsuccessiq.data.crm

import com.realtorsuccessiq.data.model.Contact
import com.realtorsuccessiq.data.model.Task
import kotlinx.coroutines.delay

class DemoConnector : CrmConnector {
    private val mockContacts = listOf(
        Contact(
            id = "demo-1",
            name = "John Smith",
            phone = "+1234567890",
            email = "john.smith@example.com",
            tags = "Past Client,Top 50",
            stage = "Past Client",
            segment = "A",
            lastContactedAt = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L,
            score = 85f
        ),
        Contact(
            id = "demo-2",
            name = "Sarah Johnson",
            phone = "+1234567891",
            email = "sarah.j@example.com",
            tags = "Hot,Builder Partner",
            stage = "Hot Lead",
            segment = "A",
            lastContactedAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L,
            score = 90f
        ),
        Contact(
            id = "demo-3",
            name = "Mike Davis",
            phone = "+1234567892",
            email = "mike.davis@example.com",
            tags = "Past Client",
            stage = "Nurture",
            segment = "B",
            lastContactedAt = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000L,
            score = 70f
        ),
        Contact(
            id = "demo-4",
            name = "Emily Chen",
            phone = "+1234567893",
            email = "emily.chen@example.com",
            tags = "Top 50",
            stage = "Active Search",
            segment = "A",
            lastContactedAt = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L,
            score = 88f
        ),
        Contact(
            id = "demo-5",
            name = "Robert Wilson",
            phone = "+1234567894",
            email = "robert.w@example.com",
            tags = "",
            stage = "Cold",
            segment = "C",
            lastContactedAt = System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000L,
            score = 50f
        )
    )
    
    override suspend fun validateConnection(): ConnectionStatus {
        delay(500) // Simulate network delay
        return ConnectionStatus.Connected
    }
    
    override suspend fun syncDownContacts(cursor: String?): SyncResult {
        delay(1000)
        return SyncResult.Success(mockContacts.size)
    }
    
    override suspend fun syncDownTasks(cursor: String?): SyncResult {
        delay(500)
        return SyncResult.Success(0)
    }
    
    override suspend fun pushCallLog(callLog: Map<String, Any>): PushResult {
        delay(300)
        return PushResult.Success
    }
    
    override suspend fun pushNote(note: Map<String, Any>): PushResult {
        delay(300)
        return PushResult.Success
    }
    
    override suspend fun pushTask(task: Map<String, Any>): PushResult {
        delay(300)
        return PushResult.Success
    }
    
    override suspend fun searchContacts(query: String): List<Contact> {
        delay(200)
        return mockContacts.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.phone?.contains(query, ignoreCase = true) == true ||
            it.email?.contains(query, ignoreCase = true) == true
        }
    }
    
    override suspend fun getContactById(id: String): Contact? {
        delay(100)
        return mockContacts.find { it.id == id }
    }
    
    fun getMockContacts(): List<Contact> = mockContacts
}

