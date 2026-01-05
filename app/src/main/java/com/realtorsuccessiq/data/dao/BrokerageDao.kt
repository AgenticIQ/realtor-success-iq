package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.Brokerage
import kotlinx.coroutines.flow.Flow

@Dao
interface BrokerageDao {
    @Query("SELECT * FROM brokerages WHERE id = :id")
    suspend fun getBrokerageById(id: String): Brokerage?
    
    @Query("SELECT * FROM brokerages WHERE adminEmail = :email")
    suspend fun getBrokerageByAdminEmail(email: String): Brokerage?
    
    @Query("SELECT * FROM brokerages WHERE id = :id")
    fun getBrokerageFlow(id: String): Flow<Brokerage?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrokerage(brokerage: Brokerage)
    
    @Update
    suspend fun updateBrokerage(brokerage: Brokerage)
}

