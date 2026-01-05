package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey val id: String,
    val type: ActivityType,
    val timestamp: Long = System.currentTimeMillis(),
    val personId: String? = null,
    val durationSeconds: Int? = null,
    val outcome: String? = null,
    val notes: String? = null,
    val starsAwarded: Int = 0,
    val synced: Boolean = false,
    val providerRefs: String? = null // JSON string for provider-specific references
)

enum class ActivityType {
    CALL_ATTEMPT,
    CONVERSATION,
    TEXT_SENT,
    APPT_ASK,
    APPT_SET,
    LISTING_APPT,
    LISTING_SIGNED,
    LEAD_GEN_SESSION,
    WEEKLY_REVIEW
}

