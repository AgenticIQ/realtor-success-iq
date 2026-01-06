package com.realtorsuccessiq.di

import android.content.Context
import androidx.room.Room
import com.realtorsuccessiq.data.dao.*
import com.realtorsuccessiq.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "realtor_success_db"
        )
        .addMigrations(
            com.realtorsuccessiq.data.database.Migrations.MIGRATION_1_2,
            com.realtorsuccessiq.data.database.Migrations.MIGRATION_2_3
        )
        .build()
    }
    
    @Provides
    fun provideUserSettingsDao(database: AppDatabase): UserSettingsDao = database.userSettingsDao()
    
    @Provides
    fun provideContactDao(database: AppDatabase): ContactDao = database.contactDao()
    
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()
    
    @Provides
    fun provideActivityLogDao(database: AppDatabase): ActivityLogDao = database.activityLogDao()
    
    @Provides
    fun provideRewardDao(database: AppDatabase): RewardDao = database.rewardDao()
    
    @Provides
    fun provideBrokerageDao(database: AppDatabase): com.realtorsuccessiq.data.dao.BrokerageDao = database.brokerageDao()
    
    @Provides
    fun provideAgentDao(database: AppDatabase): com.realtorsuccessiq.data.dao.AgentDao = database.agentDao()
    
    @Provides
    fun provideLeaderboardDao(database: AppDatabase): com.realtorsuccessiq.data.dao.LeaderboardDao = database.leaderboardDao()
    
    @Provides
    fun providePrizeDao(database: AppDatabase): com.realtorsuccessiq.data.dao.PrizeDao = database.prizeDao()
    
    @Provides
    fun providePrizeWinnerDao(database: AppDatabase): com.realtorsuccessiq.data.dao.PrizeWinnerDao = database.prizeWinnerDao()
}

