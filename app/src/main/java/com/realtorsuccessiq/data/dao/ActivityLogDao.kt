package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.ActivityLog
import com.realtorsuccessiq.data.model.ActivityType
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ActivityLog>>
    
    @Query("SELECT * FROM activity_logs WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    suspend fun getLogsInRange(startTime: Long, endTime: Long): List<ActivityLog>
    
    @Query("SELECT * FROM activity_logs WHERE type = :type AND timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getLogsByType(type: ActivityType, startTime: Long, endTime: Long): List<ActivityLog>
    
    @Query("SELECT * FROM activity_logs WHERE personId = :personId ORDER BY timestamp DESC")
    suspend fun getLogsForPerson(personId: String): List<ActivityLog>
    
    @Query("SELECT * FROM activity_logs WHERE synced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedLogs(): List<ActivityLog>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLog)
    
    @Update
    suspend fun updateLog(log: ActivityLog)
    
    @Query("UPDATE activity_logs SET synced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}
