package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.Reward
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards ORDER BY unlockedAt DESC")
    fun getAllRewards(): Flow<List<Reward>>
    
    @Query("SELECT * FROM rewards WHERE badgeType = :badgeType")
    suspend fun getRewardByType(badgeType: String): Reward?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: Reward)
}

