package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prize_winners")
data class PrizeWinner(
    @PrimaryKey val id: String,
    val prizeId: String,
    val agentId: String,
    val brokerageId: String,
    val rank: Int,
    val period: LeaderboardPeriod,
    val awardedAt: Long = System.currentTimeMillis(),
    val redeemed: Boolean = false,
    val redeemedAt: Long? = null
)

