package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey val id: String,
    val badgeType: String,
    val unlockedAt: Long = System.currentTimeMillis(),
    val metadata: String? = null // JSON string for additional badge data
)

enum class BadgeType {
    FIFTY_CONVERSATIONS_WEEK,
    TWO_LISTING_APTS_WEEK,
    TEN_DAY_RECONNECT_SPRINT,
    LEAD_GEN_STREAK_7,
    LEAD_GEN_STREAK_30,
    CALL_STREAK_7,
    CALL_STREAK_30
}
