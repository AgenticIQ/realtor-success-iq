package com.realtorsuccessiq.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create new tables for admin features
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS brokerages (
                    id TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    adminEmail TEXT NOT NULL,
                    logoUrl TEXT,
                    primaryColor INTEGER NOT NULL,
                    secondaryColor INTEGER NOT NULL,
                    accentColor INTEGER NOT NULL,
                    customBrandingEnabled INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
            """)
            
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS agents (
                    id TEXT NOT NULL PRIMARY KEY,
                    brokerageId TEXT NOT NULL,
                    email TEXT NOT NULL,
                    name TEXT NOT NULL,
                    phone TEXT,
                    profileImageUrl TEXT,
                    isActive INTEGER NOT NULL,
                    joinedAt INTEGER NOT NULL,
                    lastActiveAt INTEGER NOT NULL
                )
            """)
            
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS leaderboard_entries (
                    id TEXT NOT NULL PRIMARY KEY,
                    agentId TEXT NOT NULL,
                    brokerageId TEXT NOT NULL,
                    period TEXT NOT NULL,
                    metric TEXT NOT NULL,
                    value INTEGER NOT NULL,
                    rank INTEGER NOT NULL,
                    periodStart INTEGER NOT NULL,
                    periodEnd INTEGER NOT NULL,
                    calculatedAt INTEGER NOT NULL
                )
            """)
            
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS prizes (
                    id TEXT NOT NULL PRIMARY KEY,
                    brokerageId TEXT NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    prizeType TEXT NOT NULL,
                    metric TEXT NOT NULL,
                    period TEXT NOT NULL,
                    rankThreshold INTEGER,
                    value TEXT,
                    imageUrl TEXT,
                    isActive INTEGER NOT NULL,
                    startDate INTEGER NOT NULL,
                    endDate INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL
                )
            """)
            
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS prize_winners (
                    id TEXT NOT NULL PRIMARY KEY,
                    prizeId TEXT NOT NULL,
                    agentId TEXT NOT NULL,
                    brokerageId TEXT NOT NULL,
                    rank INTEGER NOT NULL,
                    period TEXT NOT NULL,
                    awardedAt INTEGER NOT NULL,
                    redeemed INTEGER NOT NULL,
                    redeemedAt INTEGER
                )
            """)
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Contacts: add stage column (nullable)
            database.execSQL("ALTER TABLE contacts ADD COLUMN stage TEXT")

            // User settings: add CRM focus filters
            database.execSQL("ALTER TABLE user_settings ADD COLUMN crmFocusTags TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE user_settings ADD COLUMN crmFocusStages TEXT NOT NULL DEFAULT ''")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Brokerages: add basic contact details
            database.execSQL("ALTER TABLE brokerages ADD COLUMN phone TEXT")
            database.execSQL("ALTER TABLE brokerages ADD COLUMN address TEXT")
        }
    }
}

