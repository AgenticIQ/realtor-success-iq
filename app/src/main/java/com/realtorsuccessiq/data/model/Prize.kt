package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prizes")
data class Prize(
    @PrimaryKey val id: String,
    val brokerageId: String,
    val name: String,
    val description: String,
    val prizeType: PrizeType,
    val metric: LeaderboardMetric,
    val period: LeaderboardPeriod,
    val rankThreshold: Int? = null, // e.g., top 10% = 10, top 3 = 3
    val value: String? = null, // e.g., "$100 gift card", "Trip for 2"
    val imageUrl: String? = null,
    val isActive: Boolean = true,
    val startDate: Long,
    val endDate: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class PrizeType {
    GIFT_CARD,
    TRIP,
    CASH,
    MERCHANDISE,
    EXPERIENCE,
    OTHER
}

