package com.realtorsuccessiq.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.realtorsuccessiq.data.repository.CrmRepository
import com.realtorsuccessiq.data.repository.LocalRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val crmRepository: CrmRepository,
    private val localRepository: LocalRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Sync contacts
            crmRepository.syncDownContacts()
            
            // Sync tasks
            crmRepository.syncDownTasks()
            
            // Push unsynced activity logs
            val unsyncedLogs = localRepository.getUnsyncedLogs()
            unsyncedLogs.forEach { log ->
                val callLog = mapOf(
                    "personId" to (log.personId ?: ""),
                    "timestamp" to log.timestamp,
                    "duration" to (log.durationSeconds ?: 0),
                    "notes" to (log.notes ?: "")
                )
                crmRepository.pushCallLog(callLog)
                localRepository.markSynced(listOf(log.id))
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

