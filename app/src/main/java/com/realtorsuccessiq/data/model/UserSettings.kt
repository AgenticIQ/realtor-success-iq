package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: String = "default",
    val timezone: String = "America/Vancouver",
    val dailyLeadGenMinutes: Int = 120,
    val dailyCallsTarget: Int = 10,
    val dailyTextsTarget: Int = 10,
    val dailyAppointmentAsksTarget: Int = 1,
    val weeklyConversationsTarget: Int = 50,
    val weeklyAppointmentsSetTarget: Int = 5,
    val weeklyListingAppointmentsTarget: Int = 2,
    val weeklyLocalPresenceTarget: Int = 1,
    val weeklyPartnerTouchesTarget: Int = 2,
    val quarterlyListingAppointmentsTarget: Int = 16,
    val quarterlySignedListingsTarget: Int = 6,
    val themePreset: String = "success_minimal",
    val customPrimaryColor: Long = 0xFF16A34A,
    val themeMode: String = "system", // light, dark, system
    val preferredPhoneAccountId: String? = null,
    val crmProvider: String = "demo", // demo, followupboss, salesforce
    val crmApiKey: String? = null,
    val crmFocusTags: String = "", // comma-separated list of tags to focus on (filter)
    val crmFocusStages: String = "", // comma-separated list of stages to focus on (filter)
    val lastSyncAt: Long? = null,
    val privacyMode: Boolean = false,
    val focusModeEnabled: Boolean = false,
    val biometricLockEnabled: Boolean = false,
    val demoMode: Boolean = true
)

