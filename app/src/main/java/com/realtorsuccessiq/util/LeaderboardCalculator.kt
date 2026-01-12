package com.realtorsuccessiq.util

import com.realtorsuccessiq.data.model.*
import com.realtorsuccessiq.data.repository.AdminRepository
import com.realtorsuccessiq.data.repository.LocalRepository
import java.util.*
import kotlin.math.max

class LeaderboardCalculator(
    private val adminRepository: AdminRepository,
    private val localRepository: LocalRepository
) {
    suspend fun calculateLeaderboard(
        brokerageId: String,
        period: LeaderboardPeriod,
        metric: LeaderboardMetric
    ): List<LeaderboardEntry> {
        val (periodStart, periodEnd) = getPeriodBounds(period)
        val agents = adminRepository.getAllAgents(brokerageId).filter { it.isActive }
        
        val entries = agents.mapNotNull { agent ->
            val value = calculateMetricValue(agent.id, metric, periodStart, periodEnd)
            if (value > 0) {
                LeaderboardEntry(
                    id = UUID.randomUUID().toString(),
                    agentId = agent.id,
                    brokerageId = brokerageId,
                    period = period,
                    metric = metric,
                    value = value,
                    rank = 0, // Will be set after sorting
                    periodStart = periodStart,
                    periodEnd = periodEnd
                )
            } else null
        }
        
        // Sort by value descending and assign ranks
        val sorted = entries.sortedByDescending { it.value }
        val ranked = sorted.mapIndexed { index, entry ->
            entry.copy(rank = index + 1)
        }
        
        // Save to database
        adminRepository.saveLeaderboardEntries(ranked)
        
        return ranked
    }
    
    private suspend fun calculateMetricValue(
        agentId: String,
        metric: LeaderboardMetric,
        startTime: Long,
        endTime: Long
    ): Int {
        // For MVP, we'll use activity logs. In production, this might come from a separate analytics table
        val logs = localRepository.getLogsInRange(startTime, endTime)
        // Note: In a real multi-tenant system, logs would be filtered by agentId
        // For now, we'll assume logs are agent-specific
        
        return when (metric) {
            LeaderboardMetric.TOTAL_CALLS -> {
                logs.count { it.type == ActivityType.CALL_ATTEMPT || it.type == ActivityType.CONVERSATION }
            }
            LeaderboardMetric.CONVERSATIONS -> {
                logs.count { it.type == ActivityType.CONVERSATION }
            }
            LeaderboardMetric.APPOINTMENTS_SET -> {
                logs.count { it.type == ActivityType.APPT_SET }
            }
            LeaderboardMetric.LISTING_APPOINTMENTS -> {
                logs.count { it.type == ActivityType.LISTING_APPT }
            }
            LeaderboardMetric.LISTINGS_SIGNED -> {
                logs.count { it.type == ActivityType.LISTING_SIGNED }
            }
            LeaderboardMetric.TOTAL_STARS -> {
                logs.sumOf { it.starsAwarded.toLong() }.toInt()
            }
            LeaderboardMetric.LEAD_GEN_MINUTES -> {
                logs
                    .filter { it.type == ActivityType.LEAD_GEN_SESSION }
                    .sumOf { (it.durationSeconds ?: 0) / 60L }
                    .toInt()
            }
            LeaderboardMetric.STREAK_DAYS -> {
                // Calculate streak - simplified for MVP
                calculateStreakDays(logs)
            }
        }
    }
    
    private fun calculateStreakDays(logs: List<com.realtorsuccessiq.data.model.ActivityLog>): Int {
        // Simplified streak calculation
        val daysWithActivity = logs.map { it.timestamp / (24 * 60 * 60 * 1000L) }.distinct().sorted()
        if (daysWithActivity.isEmpty()) return 0
        
        var maxStreak = 1
        var currentStreak = 1
        for (i in 1 until daysWithActivity.size) {
            if (daysWithActivity[i] == daysWithActivity[i - 1] + 1) {
                currentStreak++
                maxStreak = max(maxStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }
        return maxStreak
    }
    
    private fun getPeriodBounds(period: LeaderboardPeriod): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        
        return when (period) {
            LeaderboardPeriod.DAILY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val end = calendar.timeInMillis
                Pair(start, end)
            }
            LeaderboardPeriod.WEEKLY -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                val end = calendar.timeInMillis
                Pair(start, end)
            }
            LeaderboardPeriod.MONTHLY -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                val end = calendar.timeInMillis
                Pair(start, end)
            }
            LeaderboardPeriod.QUARTERLY -> {
                val quarter = calendar.get(Calendar.MONTH) / 3
                calendar.set(Calendar.MONTH, quarter * 3)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 3)
                val end = calendar.timeInMillis
                Pair(start, end)
            }
            LeaderboardPeriod.ANNUAL -> {
                calendar.set(Calendar.MONTH, 0)
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                calendar.add(Calendar.YEAR, 1)
                val end = calendar.timeInMillis
                Pair(start, end)
            }
        }
    }
    
    suspend fun awardPrizesForPeriod(
        brokerageId: String,
        period: LeaderboardPeriod,
        prize: Prize
    ) {
        val leaderboard = calculateLeaderboard(brokerageId, period, prize.metric)
        val agents = adminRepository.getAllAgents(brokerageId)
        val totalAgents = agents.size
        
        val winners = when {
            prize.rankThreshold != null -> {
                // Top N agents
                leaderboard.take(prize.rankThreshold).map { entry ->
                    PrizeWinner(
                        id = UUID.randomUUID().toString(),
                        prizeId = prize.id,
                        agentId = entry.agentId,
                        brokerageId = brokerageId,
                        rank = entry.rank,
                        period = period
                    )
                }
            }
            else -> {
                // Top 10% (default)
                val top10PercentCount = max(1, (totalAgents * 0.1).toInt())
                leaderboard.take(top10PercentCount).map { entry ->
                    PrizeWinner(
                        id = UUID.randomUUID().toString(),
                        prizeId = prize.id,
                        agentId = entry.agentId,
                        brokerageId = brokerageId,
                        rank = entry.rank,
                        period = period
                    )
                }
            }
        }
        
        adminRepository.awardPrizes(winners)
    }
}
