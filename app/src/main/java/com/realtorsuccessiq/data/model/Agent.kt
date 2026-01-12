package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agents")
data class Agent(
    @PrimaryKey val id: String,
    val brokerageId: String,
    val email: String,
    val name: String,
    val phone: String? = null,
    val profileImageUrl: String? = null,
    val isActive: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis()
)
