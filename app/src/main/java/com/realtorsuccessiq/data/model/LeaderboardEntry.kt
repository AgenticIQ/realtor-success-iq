package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntry(
    @PrimaryKey val id: String,
    val agentId: String,
    val brokerageId: String,
    val period: LeaderboardPeriod,
    val metric: LeaderboardMetric,
    val value: Int,
    val rank: Int,
    val periodStart: Long,
    val periodEnd: Long,
    val calculatedAt: Long = System.currentTimeMillis()
)

enum class LeaderboardPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    ANNUAL
}

enum class LeaderboardMetric {
    TOTAL_CALLS,
    CONVERSATIONS,
    APPOINTMENTS_SET,
    LISTING_APPOINTMENTS,
    LISTINGS_SIGNED,
    TOTAL_STARS,
    LEAD_GEN_MINUTES,
    STREAK_DAYS
}

