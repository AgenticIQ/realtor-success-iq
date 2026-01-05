package com.realtorsuccessiq.data.network

import retrofit2.Response
import retrofit2.http.*

interface FollowUpBossApi {
    @GET("identity")
    suspend fun getIdentity(): Response<FollowUpBossIdentity>
    
    @GET("people")
    suspend fun getPeople(@Query("cursor") cursor: String? = null): Response<List<FollowUpBossResponse>>
    
    @GET("people/{id}")
    suspend fun getPerson(@Path("id") id: String): Response<FollowUpBossResponse>
    
    @GET("tasks")
    suspend fun getTasks(@Query("cursor") cursor: String? = null): Response<List<FollowUpBossResponse>>
    
    @POST("calls")
    suspend fun postCall(@Body call: Map<String, Any>): Response<FollowUpBossResponse>
    
    @POST("notes")
    suspend fun postNote(@Body note: Map<String, Any>): Response<FollowUpBossResponse>
    
    @POST("tasks")
    suspend fun postTask(@Body task: Map<String, Any>): Response<FollowUpBossResponse>
}

data class FollowUpBossIdentity(
    val id: String?,
    val name: String?,
    val email: String?
)

data class FollowUpBossResponse(
    val id: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null
)

