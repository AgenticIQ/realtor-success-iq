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
    suspend fun getTagsWrapped(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null
    ): Response<FollowUpBossTagsResponse>

    @GET("tags")
    suspend fun getTagsList(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null
    ): Response<List<FollowUpBossTag>>
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
    @SerializedName("cursor") val cursor: String? = null,
    @SerializedName("nextCursor") val nextCursor: String? = null,
    @SerializedName("next_cursor") val nextCursorSnake: String? = null,
    @SerializedName("next") val next: String? = null
) {
    /**
     * Follow Up Boss pagination varies by endpoint/account:
     * - Some return a next cursor token (nextCursor / next_cursor)
     * - Some return a "next" link URL that includes ?cursor=... (or &cursor=...)
     * - Some may include a "cursor" field that represents the *current* cursor, not the next one
     *
     * We therefore prefer "next" cursor fields and, when given a URL, extract the cursor token.
     */
    val effectiveCursor: String?
        get() = extractCursorToken(
            nextCursor
                ?: nextCursorSnake
                ?: next
                ?: cursor
        )

    private fun extractCursorToken(value: String?): String? {
        val raw = value?.trim()?.takeIf { it.isNotBlank() } ?: return null

        // If it's already a token, return as-is.
        if (!raw.contains("cursor=", ignoreCase = true) && !raw.contains("://")) return raw

        // If it's a URL (or query string) containing cursor=, extract that param.
        val match = Regex("""[?&]cursor=([^&]+)""", RegexOption.IGNORE_CASE).find(raw)
        val token = match?.groupValues?.getOrNull(1)?.trim()?.takeIf { it.isNotBlank() }
        if (token != null) {
            // The "next" link typically URL-encodes the cursor; Retrofit will encode @Query again.
            // Decode to avoid double-encoding (e.g., %3D -> =).
            return try {
                java.net.URLDecoder.decode(token, "UTF-8")
            } catch (_: Exception) {
                token
            }
        }

        return raw
    }
}

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
    @SerializedName("results") val results: List<FollowUpBossTag>? = null,
    @SerializedName("_metadata") val metadata: FollowUpBossMetadata? = null
) {
    val items: List<FollowUpBossTag>
        get() = tags ?: results ?: emptyList()
}

data class FollowUpBossTag(
    val id: Long? = null,
    val name: String? = null
)

