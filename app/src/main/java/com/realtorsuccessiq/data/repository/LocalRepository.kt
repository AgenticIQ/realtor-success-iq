package com.realtorsuccessiq.data.repository

import com.realtorsuccessiq.data.dao.*
import com.realtorsuccessiq.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LocalRepository(
    private val userSettingsDao: UserSettingsDao,
    private val contactDao: ContactDao,
    private val taskDao: TaskDao,
    private val activityLogDao: ActivityLogDao,
    private val rewardDao: RewardDao
) {
    // UserSettings
    fun getSettings(): Flow<UserSettings?> = userSettingsDao.getSettings()
    suspend fun getSettingsSync(): UserSettings? = userSettingsDao.getSettingsSync()
    suspend fun saveSettings(settings: UserSettings) = userSettingsDao.insertSettings(settings)
    suspend fun updateSettings(settings: UserSettings) = userSettingsDao.updateSettings(settings)
    
    // Contacts
    fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()
    suspend fun getContactById(id: String): Contact? = contactDao.getContactById(id)
    suspend fun searchContacts(query: String): List<Contact> = contactDao.searchContacts(query)
    suspend fun getContactsBySegment(segment: String): List<Contact> = contactDao.getContactsBySegment(segment)
    suspend fun getContactsByTag(tag: String): List<Contact> = contactDao.getContactsByTag(tag)
    suspend fun insertContact(contact: Contact) = contactDao.insertContact(contact)
    suspend fun insertContacts(contacts: List<Contact>) = contactDao.insertContacts(contacts)
    suspend fun updateContact(contact: Contact) = contactDao.updateContact(contact)
    suspend fun deleteDemoContacts() = contactDao.deleteDemoContacts()
    
    // Tasks
    fun getPendingTasks(): Flow<List<Task>> = taskDao.getPendingTasks()
    suspend fun getTasksForPerson(personId: String): List<Task> = taskDao.getTasksForPerson(personId)
    suspend fun getOverdueTasks(): List<Task> = taskDao.getOverdueTasks()
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun insertTasks(tasks: List<Task>) = taskDao.insertTasks(tasks)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    
    // ActivityLogs
    fun getAllLogs(): Flow<List<ActivityLog>> = activityLogDao.getAllLogs()
    suspend fun getLogsInRange(startTime: Long, endTime: Long): List<ActivityLog> = 
        activityLogDao.getLogsInRange(startTime, endTime)
    suspend fun getLogsByType(type: ActivityType, startTime: Long, endTime: Long): List<ActivityLog> =
        activityLogDao.getLogsByType(type, startTime, endTime)
    suspend fun getLogsForPerson(personId: String): List<ActivityLog> = 
        activityLogDao.getLogsForPerson(personId)
    suspend fun getUnsyncedLogs(): List<ActivityLog> = activityLogDao.getUnsyncedLogs()
    suspend fun insertLog(log: ActivityLog) = activityLogDao.insertLog(log)
    suspend fun markSynced(ids: List<String>) = activityLogDao.markSynced(ids)
    
    // Rewards
    fun getAllRewards(): Flow<List<Reward>> = rewardDao.getAllRewards()
    suspend fun getRewardByType(badgeType: String): Reward? = rewardDao.getRewardByType(badgeType)
    suspend fun insertReward(reward: Reward) = rewardDao.insertReward(reward)
    
    // Helper to create activity log with auto-generated ID
    suspend fun logActivity(
        type: ActivityType,
        personId: String? = null,
        durationSeconds: Int? = null,
        outcome: String? = null,
        notes: String? = null,
        starsAwarded: Int = 0
    ): ActivityLog {
        val log = ActivityLog(
            id = UUID.randomUUID().toString(),
            type = type,
            personId = personId,
            durationSeconds = durationSeconds,
            outcome = outcome,
            notes = notes,
            starsAwarded = starsAwarded
        )
        insertLog(log)
        return log
    }
}

