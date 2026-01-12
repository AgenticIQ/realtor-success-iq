package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.Agent
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents WHERE brokerageId = :brokerageId AND isActive = 1 ORDER BY name ASC")
    fun getAgentsByBrokerage(brokerageId: String): Flow<List<Agent>>
    
    @Query("SELECT * FROM agents WHERE id = :id")
    suspend fun getAgentById(id: String): Agent?
    
    @Query("SELECT * FROM agents WHERE email = :email")
    suspend fun getAgentByEmail(email: String): Agent?
    
    @Query("SELECT * FROM agents WHERE brokerageId = :brokerageId")
    suspend fun getAllAgentsByBrokerage(brokerageId: String): List<Agent>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: Agent)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgents(agents: List<Agent>)
    
    @Update
    suspend fun updateAgent(agent: Agent)
    
    @Query("UPDATE agents SET isActive = 0 WHERE id = :agentId")
    suspend fun deactivateAgent(agentId: String)
}
