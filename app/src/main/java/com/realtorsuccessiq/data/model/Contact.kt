package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String?,
    val email: String?,
    val tags: String = "", // comma-separated
    val stage: String? = null, // CRM pipeline stage (if available)
    val segment: String = "C", // A, B, or C
    val lastContactedAt: Long? = null,
    val score: Float = 0f,
    val rawJson: String? = null,
    val providerId: String? = null, // CRM provider's ID
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getTagsList(): List<String> {
        return if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }
    }
}

