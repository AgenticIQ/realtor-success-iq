package com.realtorsuccessiq.data.network

import retrofit2.Response
import retrofit2.http.*
import com.google.gson.annotations.SerializedName

interface FollowUpBossApi {
    @GET("identity")
    suspend fun getIdentity(): Response<FollowUpBossIdentity>
    
    @GET("people")
    suspend fun getPeople(@Query("cursor") cursor: String? = null): Response<FollowUpBossPeopleResponse>
    
    @GET("people/{id}")
    suspend fun getPerson(@Path("id") id: String): Response<FollowUpBossPerson>
    
    @GET("tasks")
    suspend fun getTasks(@Query("cursor") cursor: String? = null): Response<FollowUpBossTasksResponse>
    
    @POST("calls")
    suspend fun postCall(@Body call: Map<String, Any>): Response<FollowUpBossGenericResponse>
    
    @POST("notes")
    suspend fun postNote(@Body note: Map<String, Any>): Response<FollowUpBossGenericResponse>
    
    @POST("tasks")
    suspend fun postTask(@Body task: Map<String, Any>): Response<FollowUpBossGenericResponse>
}

data class FollowUpBossIdentity(
    val id: String?,
    val name: String?,
    val email: String?
)

data class FollowUpBossPeopleResponse(
    val people: List<FollowUpBossPerson> = emptyList(),
    @SerializedName("_metadata") val metadata: FollowUpBossMetadata? = null
)

data class FollowUpBossTasksResponse(
    val tasks: List<FollowUpBossTask> = emptyList(),
    @SerializedName("_metadata") val metadata: FollowUpBossMetadata? = null
)

data class FollowUpBossMetadata(
    val cursor: String? = null
)

data class FollowUpBossPerson(
    val id: Long? = null,
    val name: String? = null,
    val stage: String? = null,
    val tags: List<String>? = null,
    val phones: List<FollowUpBossPhone>? = null,
    val emails: List<FollowUpBossEmail>? = null
)

data class FollowUpBossPhone(
    val value: String? = null,
    val type: String? = null
)

data class FollowUpBossEmail(
    val value: String? = null,
    val type: String? = null
)

data class FollowUpBossTask(
    val id: Long? = null,
    val title: String? = null
)

data class FollowUpBossGenericResponse(
    val id: String? = null
)

