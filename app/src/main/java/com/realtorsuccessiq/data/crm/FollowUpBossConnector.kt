package com.realtorsuccessiq.data.crm

import com.realtorsuccessiq.data.model.Contact
import com.realtorsuccessiq.data.model.Task
import com.realtorsuccessiq.data.network.FollowUpBossApi
import com.realtorsuccessiq.data.network.FollowUpBossTag
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

    private val peopleFields = "id,name,phones,emails,tags,stage"
    
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
            val people = fetchAllPeople()
            SyncResult.Success(people.size)
        } catch (e: RateLimitException) {
            SyncResult.RateLimited
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Sync failed")
        }
    }
    
    override suspend fun syncDownTasks(cursor: String?): SyncResult {
        return try {
            val response = api.getTasks(cursor)
            if (response.isSuccessful) {
                val body = response.body()
                SyncResult.Success(body?.items?.size ?: 0)
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
            val people = fetchAllPeople()
            val contacts = people.mapNotNull { mapPersonToContact(it) }
            if (query.isBlank()) contacts
            else contacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.phone?.contains(query, ignoreCase = true) == true ||
                    it.email?.contains(query, ignoreCase = true) == true
            }
        } catch (_: Exception) {
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

    override suspend fun fetchAllTags(): List<String> {
        // Fetch full tag catalog with cursor pagination so the list isn't capped.
        return try {
            fetchAllTagsPaged()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchAllTagsPaged(): List<String> {
        val all = mutableListOf<String>()
        var cursor: String? = null
        val seenCursors = mutableSetOf<String>()
        var pages = 0

        while (pages < 200) {
            pages++
            val wrapped = api.getTagsWrapped(cursor = cursor, limit = 200)
            if (wrapped.code() == 429) throw RateLimitException()

            if (wrapped.isSuccessful) {
                val body = wrapped.body() ?: break
                val names = body.items.mapNotNull { it.name?.trim() }.filter { it.isNotBlank() }
                all.addAll(names)

                val next = body.metadata?.cursor?.takeIf { it.isNotBlank() } ?: break
                if (!seenCursors.add(next)) break
                cursor = next
                continue
            }

            // Fallback: some accounts/endpoints may return a raw array instead of wrapper JSON.
            val raw = api.getTagsList(cursor = cursor, limit = 200)
            if (raw.code() == 429) throw RateLimitException()
            if (!raw.isSuccessful) {
                throw RuntimeException("Failed to fetch tags: ${raw.code()}")
            }
            val list = raw.body().orEmpty()
            val names = list.mapNotNull { it.name?.trim() }.filter { it.isNotBlank() }
            all.addAll(names)

            // If it's a raw array response, we don't have cursor metadata; treat it as complete.
            break
        }

        return all
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
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
            stage = stage?.trim(),
            segment = "C",
            providerId = pid,
            updatedAt = System.currentTimeMillis()
        )
    }

    private class RateLimitException : RuntimeException()

    private suspend fun fetchAllPeople(): List<FollowUpBossPerson> {
        val all = mutableListOf<FollowUpBossPerson>()
        var cursor: String? = null

        // Safety limit to prevent infinite loops if cursor repeats
        val seenCursors = mutableSetOf<String>()
        var pages = 0

        while (pages < 200) {
            pages++
            val response = api.getPeople(cursor = cursor, fields = peopleFields, limit = 200)
            if (response.code() == 429) throw RateLimitException()
            if (!response.isSuccessful) {
                throw RuntimeException("Failed to fetch people: ${response.code()}")
            }
            val body = response.body() ?: break
            val items = body.items
            if (items.isEmpty()) break
            all.addAll(items)

            val next = body.metadata?.cursor?.takeIf { it.isNotBlank() } ?: break
            if (!seenCursors.add(next)) break
            cursor = next
        }

        return all
    }
}

