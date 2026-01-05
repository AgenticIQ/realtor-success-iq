package com.realtorsuccessiq.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brokerages")
data class Brokerage(
    @PrimaryKey val id: String,
    val name: String,
    val adminEmail: String,
    val logoUrl: String? = null,
    val primaryColor: Long = 0xFF16A34A, // ARGB
    val secondaryColor: Long = 0xFF0B1220,
    val accentColor: Long = 0xFFD4AF37,
    val customBrandingEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

