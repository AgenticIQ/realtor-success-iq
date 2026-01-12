package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.Prize
import kotlinx.coroutines.flow.Flow

@Dao
interface PrizeDao {
    @Query("SELECT * FROM prizes WHERE brokerageId = :brokerageId AND isActive = 1 ORDER BY createdAt DESC")
    fun getActivePrizes(brokerageId: String): Flow<List<Prize>>
    
    @Query("SELECT * FROM prizes WHERE id = :id")
    suspend fun getPrizeById(id: String): Prize?
    
    @Query("SELECT * FROM prizes WHERE brokerageId = :brokerageId ORDER BY createdAt DESC")
    fun getAllPrizes(brokerageId: String): Flow<List<Prize>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrize(prize: Prize)
    
    @Update
    suspend fun updatePrize(prize: Prize)
    
    @Query("UPDATE prizes SET isActive = 0 WHERE id = :prizeId")
    suspend fun deactivatePrize(prizeId: String)
}
