package com.realtorsuccessiq.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.realtorsuccessiq.data.dao.*
import com.realtorsuccessiq.data.model.*

@Database(
    entities = [
        UserSettings::class, 
        Contact::class, 
        Task::class, 
        ActivityLog::class, 
        Reward::class,
        com.realtorsuccessiq.data.model.Brokerage::class,
        com.realtorsuccessiq.data.model.Agent::class,
        com.realtorsuccessiq.data.model.LeaderboardEntry::class,
        com.realtorsuccessiq.data.model.Prize::class,
        com.realtorsuccessiq.data.model.PrizeWinner::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun contactDao(): ContactDao
    abstract fun taskDao(): TaskDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun rewardDao(): RewardDao
    abstract fun brokerageDao(): com.realtorsuccessiq.data.dao.BrokerageDao
    abstract fun agentDao(): com.realtorsuccessiq.data.dao.AgentDao
    abstract fun leaderboardDao(): com.realtorsuccessiq.data.dao.LeaderboardDao
    abstract fun prizeDao(): com.realtorsuccessiq.data.dao.PrizeDao
    abstract fun prizeWinnerDao(): com.realtorsuccessiq.data.dao.PrizeWinnerDao
}

