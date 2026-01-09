package com.realtorsuccessiq.data.network

import retrofit2.Response
import retrofit2.http.*
import com.google.gson.annotations.SerializedName

interface FollowUpBossApi {
    @GET("identity")
    suspend fun getIdentity(): Response<FollowUpBossIdentity>
    
    @GET("people")
    suspend fun getPeople(
        @Query("cursor") cursor: String? = null,
        @Query("fields") fields: String? = null,
        @Query("limit") limit: Int? = null
    ): Response<FollowUpBossPeopleResponse>
    
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

    @GET("tags")
    suspend fun getTagsWrapped(): Response<FollowUpBossTagsResponse>

    @GET("tags")
    suspend fun getTagsList(): Response<List<FollowUpBossTag>>
}

data class FollowUpBossIdentity(
    val id: String?,
    val name: String?,
    val email: String?
)

data class FollowUpBossPeopleResponse(
    @SerializedName("people") val people: List<FollowUpBossPerson>? = null,
    // Some APIs use "results" as the list wrapper; accept both to be resilient.
    @SerializedName("results") val results: List<FollowUpBossPerson>? = null,
    @SerializedName("_metadata") val metadata: FollowUpBossMetadata? = null
) {
    val items: List<FollowUpBossPerson>
        get() = people ?: results ?: emptyList()
}

data class FollowUpBossTasksResponse(
    @SerializedName("tasks") val tasks: List<FollowUpBossTask>? = null,
    @SerializedName("results") val results: List<FollowUpBossTask>? = null,
    @SerializedName("_metadata") val metadata: FollowUpBossMetadata? = null
) {
    val items: List<FollowUpBossTask>
        get() = tasks ?: results ?: emptyList()
}

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

data class FollowUpBossTagsResponse(
    @SerializedName("tags") val tags: List<FollowUpBossTag>? = null,
    @SerializedName("results") val results: List<FollowUpBossTag>? = null
) {
    val items: List<FollowUpBossTag>
        get() = tags ?: results ?: emptyList()
}

data class FollowUpBossTag(
    val name: String? = null
)

