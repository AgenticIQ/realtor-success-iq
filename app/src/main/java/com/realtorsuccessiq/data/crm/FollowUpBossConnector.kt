package com.realtorsuccessiq.data.crm

import com.realtorsuccessiq.data.model.Contact
import com.realtorsuccessiq.data.model.Task
import com.realtorsuccessiq.data.network.FollowUpBossApi
import com.realtorsuccessiq.data.network.FollowUpBossPerson
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class FollowUpBossConnector(
    private val apiKey: String,
    private val baseUrl: String = "https://api.followupboss.com/v1/"
) : CrmConnector {
    
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", Credentials.basic(apiKey, ""))
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val api = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FollowUpBossApi::class.java)
    
    override suspend fun validateConnection(): ConnectionStatus {
        return try {
            val response = api.getIdentity()
            if (response.isSuccessful) {
                ConnectionStatus.Connected
            } else {
                ConnectionStatus.Error("Invalid credentials: ${response.code()}")
            }
        } catch (e: Exception) {
            ConnectionStatus.Error(e.message ?: "Connection failed")
        }
    }
    
    override suspend fun syncDownContacts(cursor: String?): SyncResult {
        return try {
            val response = api.getPeople(cursor)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    SyncResult.Success(body.people.size)
                } else {
                    SyncResult.Success(0)
                }
            } else if (response.code() == 429) {
                SyncResult.RateLimited
            } else {
                SyncResult.Error("Failed to sync contacts: ${response.code()}")
            }
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Sync failed")
        }
    }
    
    override suspend fun syncDownTasks(cursor: String?): SyncResult {
        return try {
            val response = api.getTasks(cursor)
            if (response.isSuccessful) {
                val body = response.body()
                SyncResult.Success(body?.tasks?.size ?: 0)
            } else if (response.code() == 429) {
                SyncResult.RateLimited
            } else {
                SyncResult.Error("Failed to sync tasks: ${response.code()}")
            }
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Sync failed")
        }
    }
    
    override suspend fun pushCallLog(callLog: Map<String, Any>): PushResult {
        return try {
            val response = api.postCall(callLog)
            if (response.isSuccessful) {
                PushResult.Success
            } else {
                PushResult.Error("Failed to push call: ${response.code()}")
            }
        } catch (e: Exception) {
            PushResult.Error(e.message ?: "Push failed")
        }
    }
    
    override suspend fun pushNote(note: Map<String, Any>): PushResult {
        return try {
            val response = api.postNote(note)
            if (response.isSuccessful) {
                PushResult.Success
            } else {
                PushResult.Error("Failed to push note: ${response.code()}")
            }
        } catch (e: Exception) {
            PushResult.Error(e.message ?: "Push failed")
        }
    }
    
    override suspend fun pushTask(task: Map<String, Any>): PushResult {
        return try {
            val response = api.postTask(task)
            if (response.isSuccessful) {
                PushResult.Success
            } else {
                PushResult.Error("Failed to push task: ${response.code()}")
            }
        } catch (e: Exception) {
            PushResult.Error(e.message ?: "Push failed")
        }
    }
    
    override suspend fun searchContacts(query: String): List<Contact> {
        return try {
            val response = api.getPeople()
            if (response.isSuccessful) {
                val body = response.body()
                val people = body?.people.orEmpty()
                people
                    .mapNotNull { mapPersonToContact(it) }
                    .filter {
                        if (query.isBlank()) true
                        else it.name.contains(query, ignoreCase = true) ||
                            it.phone?.contains(query, ignoreCase = true) == true ||
                            it.email?.contains(query, ignoreCase = true) == true
                    }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getContactById(id: String): Contact? {
        return try {
            val fubId = id.removePrefix("fub-")
            val response = api.getPerson(fubId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) mapPersonToContact(body) else null
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun mapPersonToContact(person: FollowUpBossPerson): Contact? {
        val pid = person.id?.toString()?.takeIf { it.isNotBlank() } ?: return null
        val name = person.name?.takeIf { it.isNotBlank() } ?: "Unknown"
        val phone = person.phones?.firstOrNull()?.value
        val email = person.emails?.firstOrNull()?.value
        val tags = person.tags?.filterNotNull()?.joinToString(",") ?: ""
        val stage = person.stage

        return Contact(
            id = "fub-$pid",
            name = name,
            phone = phone,
            email = email,
            tags = tags,
            stage = stage,
            segment = "C",
            providerId = pid,
            updatedAt = System.currentTimeMillis()
        )
    }
}

