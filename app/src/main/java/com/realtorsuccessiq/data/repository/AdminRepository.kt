package com.realtorsuccessiq.data.repository

import com.realtorsuccessiq.data.dao.*
import com.realtorsuccessiq.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AdminRepository(
    private val brokerageDao: BrokerageDao,
    private val agentDao: AgentDao,
    private val leaderboardDao: LeaderboardDao,
    private val prizeDao: PrizeDao,
    private val prizeWinnerDao: PrizeWinnerDao,
    private val activityLogDao: ActivityLogDao
) {
    // Brokerage
    suspend fun getBrokerage(id: String): Brokerage? = brokerageDao.getBrokerageById(id)
    fun getBrokerageFlow(id: String): Flow<Brokerage?> = brokerageDao.getBrokerageFlow(id)
    suspend fun getBrokerageByEmail(email: String): Brokerage? = brokerageDao.getBrokerageByAdminEmail(email)
    suspend fun saveBrokerage(brokerage: Brokerage) = brokerageDao.insertBrokerage(brokerage)
    suspend fun updateBrokerage(brokerage: Brokerage) = brokerageDao.updateBrokerage(brokerage)
    
    // Agents
    fun getAgents(brokerageId: String): Flow<List<Agent>> = agentDao.getAgentsByBrokerage(brokerageId)
    suspend fun getAgent(id: String): Agent? = agentDao.getAgentById(id)
    suspend fun getAgentByEmail(email: String): Agent? = agentDao.getAgentByEmail(email)
    suspend fun addAgent(agent: Agent) = agentDao.insertAgent(agent)
    suspend fun updateAgent(agent: Agent) = agentDao.updateAgent(agent)
    suspend fun deactivateAgent(agentId: String) = agentDao.deactivateAgent(agentId)
    suspend fun getAllAgents(brokerageId: String): List<Agent> = agentDao.getAllAgentsByBrokerage(brokerageId)
    
    // Leaderboards
    suspend fun getLeaderboard(
        brokerageId: String,
        period: LeaderboardPeriod,
        metric: LeaderboardMetric,
        limit: Int = 100
    ): List<LeaderboardEntry> = leaderboardDao.getLeaderboard(brokerageId, period, metric, limit = limit)
    
    suspend fun getAgentRank(
        agentId: String,
        period: LeaderboardPeriod,
        metric: LeaderboardMetric
    ): LeaderboardEntry? = leaderboardDao.getAgentRank(agentId, period, metric)
    
    suspend fun saveLeaderboardEntries(entries: List<LeaderboardEntry>) = leaderboardDao.insertEntries(entries)
    
    // Prizes
    fun getActivePrizes(brokerageId: String): Flow<List<Prize>> = prizeDao.getActivePrizes(brokerageId)
    fun getAllPrizes(brokerageId: String): Flow<List<Prize>> = prizeDao.getAllPrizes(brokerageId)
    suspend fun getPrize(id: String): Prize? = prizeDao.getPrizeById(id)
    suspend fun createPrize(prize: Prize) = prizeDao.insertPrize(prize)
    suspend fun updatePrize(prize: Prize) = prizeDao.updatePrize(prize)
    suspend fun deactivatePrize(prizeId: String) = prizeDao.deactivatePrize(prizeId)
    
    // Prize Winners
    fun getWinnersForPrize(prizeId: String): Flow<List<PrizeWinner>> = prizeWinnerDao.getWinnersForPrize(prizeId)
    fun getWinnersForAgent(agentId: String): Flow<List<PrizeWinner>> = prizeWinnerDao.getWinnersForAgent(agentId)
    fun getUnredeemedWinners(brokerageId: String): Flow<List<PrizeWinner>> = prizeWinnerDao.getUnredeemedWinners(brokerageId)
    suspend fun awardPrize(winner: PrizeWinner) = prizeWinnerDao.insertWinner(winner)
    suspend fun awardPrizes(winners: List<PrizeWinner>) = prizeWinnerDao.insertWinners(winners)
    suspend fun markPrizeRedeemed(winnerId: String) = prizeWinnerDao.markRedeemed(winnerId)
    
    // Analytics
    suspend fun getAgentActivityStats(
        agentId: String,
        startTime: Long,
        endTime: Long
    ): AgentStats {
        val logs = activityLogDao.getLogsInRange(startTime, endTime)
        val agentLogs = logs.filter { it.personId == agentId || it.personId == null }
        
        return AgentStats(
            totalCalls = agentLogs.count { it.type == ActivityType.CALL_ATTEMPT || it.type == ActivityType.CONVERSATION },
            conversations = agentLogs.count { it.type == ActivityType.CONVERSATION },
            appointmentsSet = agentLogs.count { it.type == ActivityType.APPT_SET },
            listingAppointments = agentLogs.count { it.type == ActivityType.LISTING_APPT },
            listingsSigned = agentLogs.count { it.type == ActivityType.LISTING_SIGNED },
            totalStars = agentLogs.sumOf { it.starsAwarded.toLong() }.toInt(),
            leadGenMinutes = agentLogs
                .filter { it.type == ActivityType.LEAD_GEN_SESSION }
                .sumOf { (it.durationSeconds ?: 0) / 60L }
        )
    }
}

data class AgentStats(
    val totalCalls: Int,
    val conversations: Int,
    val appointmentsSet: Int,
    val listingAppointments: Int,
    val listingsSigned: Int,
    val totalStars: Int,
    val leadGenMinutes: Long
)

