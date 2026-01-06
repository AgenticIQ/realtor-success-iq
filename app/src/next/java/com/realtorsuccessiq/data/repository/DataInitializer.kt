package com.realtorsuccessiq.data.repository

import com.realtorsuccessiq.data.model.UserSettings

/**
 * NEXT flavor initializer:
 * - Do NOT seed demo contacts (so real CRM testing isn't mixed with mock data).
 * - Still ensures settings exist on first launch.
 */
class DataInitializer(
    private val localRepository: LocalRepository,
    private val adminRepository: AdminRepository? = null
) {
    suspend fun initializeIfNeeded() {
        val settings = localRepository.getSettingsSync()
        if (settings == null) {
            localRepository.saveSettings(
                UserSettings(
                    demoMode = false,
                    crmProvider = "followupboss"
                )
            )
        }

        // Keep broker-admin demo seeding out of NEXT until real onboarding is defined.
        // (Admin features remain available; they just start empty.)
        @Suppress("UNUSED_VARIABLE")
        val unused = adminRepository
    }
}


