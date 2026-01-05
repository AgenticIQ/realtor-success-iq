package com.realtorsuccessiq.data.crm

import com.realtorsuccessiq.data.model.Contact
import com.realtorsuccessiq.data.model.Task
import com.realtorsuccessiq.data.network.FollowUpBossApi
import com.realtorsuccessiq.data.network.FollowUpBossResponse
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
                    // Map FUB people to Contact entities
                    // This is a simplified mapping - adjust based on actual FUB API response
                    SyncResult.Success(body.size)
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
                SyncResult.Success(body?.size ?: 0)
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
                // Filter and map to Contact entities
                emptyList() // Simplified - implement full mapping
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getContactById(id: String): Contact? {
        return try {
            val response = api.getPerson(id)
            if (response.isSuccessful) {
                // Map FUB person to Contact
                null // Simplified - implement full mapping
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

