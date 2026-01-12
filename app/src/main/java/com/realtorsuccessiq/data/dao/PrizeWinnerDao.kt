package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.PrizeWinner
import kotlinx.coroutines.flow.Flow

@Dao
interface PrizeWinnerDao {
    @Query("SELECT * FROM prize_winners WHERE prizeId = :prizeId ORDER BY rank ASC")
    fun getWinnersForPrize(prizeId: String): Flow<List<PrizeWinner>>
    
    @Query("SELECT * FROM prize_winners WHERE agentId = :agentId ORDER BY awardedAt DESC")
    fun getWinnersForAgent(agentId: String): Flow<List<PrizeWinner>>
    
    @Query("SELECT * FROM prize_winners WHERE brokerageId = :brokerageId AND redeemed = 0 ORDER BY awardedAt DESC")
    fun getUnredeemedWinners(brokerageId: String): Flow<List<PrizeWinner>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWinner(winner: PrizeWinner)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWinners(winners: List<PrizeWinner>)
    
    @Query("UPDATE prize_winners SET redeemed = 1, redeemedAt = :timestamp WHERE id = :winnerId")
    suspend fun markRedeemed(winnerId: String, timestamp: Long = System.currentTimeMillis())
}
