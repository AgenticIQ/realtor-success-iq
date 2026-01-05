package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE status = 'pending' ORDER BY dueAt ASC")
    fun getPendingTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE personId = :personId AND status = 'pending' ORDER BY dueAt ASC")
    suspend fun getTasksForPerson(personId: String): List<Task>
    
    @Query("SELECT * FROM tasks WHERE dueAt < :now AND status = 'pending' ORDER BY dueAt ASC")
    suspend fun getOverdueTasks(now: Long = System.currentTimeMillis()): List<Task>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
}

