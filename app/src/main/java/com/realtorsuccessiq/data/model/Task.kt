package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String,
    val personId: String?,
    val dueAt: Long,
    val type: String, // call, text, email, appointment, etc.
    val status: String = "pending", // pending, completed, cancelled
    val title: String,
    val notes: String? = null,
    val providerId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

