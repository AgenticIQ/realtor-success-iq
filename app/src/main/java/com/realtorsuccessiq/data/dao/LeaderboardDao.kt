package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.LeaderboardEntry
import com.realtorsuccessiq.data.model.LeaderboardMetric
import com.realtorsuccessiq.data.model.LeaderboardPeriod
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {
    @Query("""
        SELECT * FROM leaderboard_entries 
        WHERE brokerageId = :brokerageId 
        AND period = :period 
        AND metric = :metric
        AND periodStart <= :now AND periodEnd >= :now
        ORDER BY rank ASC
        LIMIT :limit
    """)
    suspend fun getLeaderboard(
        brokerageId: String,
        period: LeaderboardPeriod,
        metric: LeaderboardMetric,
        now: Long = System.currentTimeMillis(),
        limit: Int = 100
    ): List<LeaderboardEntry>
    
    @Query("""
        SELECT * FROM leaderboard_entries 
        WHERE agentId = :agentId 
        AND period = :period 
        AND metric = :metric
        AND periodStart <= :now AND periodEnd >= :now
        LIMIT 1
    """)
    suspend fun getAgentRank(
        agentId: String,
        period: LeaderboardPeriod,
        metric: LeaderboardMetric,
        now: Long = System.currentTimeMillis()
    ): LeaderboardEntry?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LeaderboardEntry)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<LeaderboardEntry>)
    
    @Query("DELETE FROM leaderboard_entries WHERE brokerageId = :brokerageId AND period = :period AND periodStart < :before")
    suspend fun deleteOldEntries(brokerageId: String, period: LeaderboardPeriod, before: Long)
}
