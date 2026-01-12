package com.realtorsuccessiq.util

import com.realtorsuccessiq.data.model.ActivityType
import com.realtorsuccessiq.data.model.BadgeType
import com.realtorsuccessiq.data.model.Reward
import com.realtorsuccessiq.data.repository.LocalRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class GamificationEngine(
    private val localRepository: LocalRepository
) {
    fun getStarsForActivity(type: ActivityType): Int {
        return when (type) {
            ActivityType.CALL_ATTEMPT -> 1
            ActivityType.CONVERSATION -> 3
            ActivityType.APPT_ASK -> 5
            ActivityType.APPT_SET -> 15
            ActivityType.LISTING_APPT -> 25
            ActivityType.LISTING_SIGNED -> 100
            ActivityType.WEEKLY_REVIEW -> 20
            else -> 0
        }
    }
    
    suspend fun checkAndAwardBadges(
        weeklyConversations: Int,
        weeklyListingAppts: Int,
        leadGenStreak: Int,
        callStreak: Int
    ) {
        // Check for badges
        if (weeklyConversations >= 50) {
            awardBadgeIfNotExists(BadgeType.FIFTY_CONVERSATIONS_WEEK)
        }
        if (weeklyListingAppts >= 2) {
            awardBadgeIfNotExists(BadgeType.TWO_LISTING_APTS_WEEK)
        }
        if (leadGenStreak >= 7) {
            awardBadgeIfNotExists(BadgeType.LEAD_GEN_STREAK_7)
        }
        if (leadGenStreak >= 30) {
            awardBadgeIfNotExists(BadgeType.LEAD_GEN_STREAK_30)
        }
        if (callStreak >= 7) {
            awardBadgeIfNotExists(BadgeType.CALL_STREAK_7)
        }
        if (callStreak >= 30) {
            awardBadgeIfNotExists(BadgeType.CALL_STREAK_30)
        }
    }
    
    private suspend fun awardBadgeIfNotExists(badgeType: BadgeType) {
        val existing = localRepository.getRewardByType(badgeType.name)
        if (existing == null) {
            localRepository.insertReward(
                Reward(
                    id = UUID.randomUUID().toString(),
                    badgeType = badgeType.name
                )
            )
        }
    }
    
    suspend fun calculateStreaks(): StreakData {
        val now = System.currentTimeMillis()
        val oneDayAgo = now - 24 * 60 * 60 * 1000L
        val oneWeekAgo = now - 7 * 24 * 60 * 60 * 1000L
        
        val allLogs = localRepository.getAllLogs().first()
        
        // Lead gen streak: consecutive days with LEAD_GEN_SESSION
        var leadGenStreak = 0
        var currentDay = now
        while (true) {
            val dayStart = currentDay - (currentDay % (24 * 60 * 60 * 1000L))
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L
            val hasLeadGen = allLogs.any {
                it.type == ActivityType.LEAD_GEN_SESSION &&
                it.timestamp >= dayStart && it.timestamp < dayEnd
            }
            if (hasLeadGen) {
                leadGenStreak++
                currentDay = dayStart - 1
            } else {
                break
            }
        }
        
        // Call streak: consecutive days with at least one call
        var callStreak = 0
        currentDay = now
        while (true) {
            val dayStart = currentDay - (currentDay % (24 * 60 * 60 * 1000L))
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L
            val hasCall = allLogs.any {
                (it.type == ActivityType.CALL_ATTEMPT || it.type == ActivityType.CONVERSATION) &&
                it.timestamp >= dayStart && it.timestamp < dayEnd
            }
            if (hasCall) {
                callStreak++
                currentDay = dayStart - 1
            } else {
                break
            }
        }
        
        return StreakData(leadGenStreak, callStreak)
    }
}

data class StreakData(
    val leadGenStreak: Int,
    val callStreak: Int
)
