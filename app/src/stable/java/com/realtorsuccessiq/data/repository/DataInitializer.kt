package com.realtorsuccessiq.data.repository

import com.realtorsuccessiq.data.crm.DemoConnector
import com.realtorsuccessiq.data.model.*
import kotlinx.coroutines.flow.first
import java.util.UUID

class DataInitializer(
    private val localRepository: LocalRepository,
    private val adminRepository: AdminRepository? = null
) {
    suspend fun initializeIfNeeded() {
        val settings = localRepository.getSettingsSync()
        if (settings == null) {
            // First launch - initialize with defaults
            localRepository.saveSettings(UserSettings())

            // Load demo contacts if in demo mode
            val demoConnector = DemoConnector()
            val mockContacts = demoConnector.getMockContacts()
            localRepository.insertContacts(mockContacts)
        }

        // Initialize demo brokerage for testing (if admin repository is available)
        try {
            adminRepository?.let { repo ->
                val existingBrokerage = repo.getBrokerageByEmail("admin@testbrokerage.com")
                if (existingBrokerage == null) {
                    val demoBrokerage = Brokerage(
                        id = UUID.randomUUID().toString(),
                        name = "Demo Realty Group",
                        adminEmail = "admin@testbrokerage.com",
                        primaryColor = 0xFF16A34A,
                        secondaryColor = 0xFF0B1220,
                        accentColor = 0xFFD4AF37
                    )
                    repo.saveBrokerage(demoBrokerage)

                    // Add demo agents
                    val demoAgents = listOf(
                        Agent(
                            id = UUID.randomUUID().toString(),
                            brokerageId = demoBrokerage.id,
                            email = "agent1@test.com",
                            name = "John Smith",
                            phone = "+1234567890",
                            isActive = true
                        ),
                        Agent(
                            id = UUID.randomUUID().toString(),
                            brokerageId = demoBrokerage.id,
                            email = "agent2@test.com",
                            name = "Sarah Johnson",
                            phone = "+1234567891",
                            isActive = true
                        ),
                        Agent(
                            id = UUID.randomUUID().toString(),
                            brokerageId = demoBrokerage.id,
                            email = "agent3@test.com",
                            name = "Mike Davis",
                            phone = "+1234567892",
                            isActive = true
                        )
                    )
                    demoAgents.forEach { repo.addAgent(it) }
                }
            }
        } catch (e: Exception) {
            // Admin repository might not be initialized yet, that's okay
        }
    }
}

