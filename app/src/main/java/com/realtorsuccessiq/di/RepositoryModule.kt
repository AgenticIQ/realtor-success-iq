package com.realtorsuccessiq.di

import com.realtorsuccessiq.data.dao.*
import com.realtorsuccessiq.data.repository.CrmRepository
import com.realtorsuccessiq.data.repository.LocalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideLocalRepository(
        userSettingsDao: UserSettingsDao,
        contactDao: ContactDao,
        taskDao: TaskDao,
        activityLogDao: ActivityLogDao,
        rewardDao: RewardDao
    ): LocalRepository {
        return LocalRepository(
            userSettingsDao,
            contactDao,
            taskDao,
            activityLogDao,
            rewardDao
        )
    }
    
    @Provides
    @Singleton
    fun provideCrmRepository(
        localRepository: LocalRepository
    ): CrmRepository {
        return CrmRepository(localRepository)
    }
    
    @Provides
    @Singleton
    fun provideAdminRepository(
        brokerageDao: com.realtorsuccessiq.data.dao.BrokerageDao,
        agentDao: com.realtorsuccessiq.data.dao.AgentDao,
        leaderboardDao: com.realtorsuccessiq.data.dao.LeaderboardDao,
        prizeDao: com.realtorsuccessiq.data.dao.PrizeDao,
        prizeWinnerDao: com.realtorsuccessiq.data.dao.PrizeWinnerDao,
        activityLogDao: com.realtorsuccessiq.data.dao.ActivityLogDao
    ): com.realtorsuccessiq.data.repository.AdminRepository {
        return com.realtorsuccessiq.data.repository.AdminRepository(
            brokerageDao,
            agentDao,
            leaderboardDao,
            prizeDao,
            prizeWinnerDao,
            activityLogDao
        )
    }
    
    @Provides
    @Singleton
    fun provideDataInitializer(
        localRepository: LocalRepository,
        adminRepository: com.realtorsuccessiq.data.repository.AdminRepository?
    ): com.realtorsuccessiq.data.repository.DataInitializer {
        return com.realtorsuccessiq.data.repository.DataInitializer(localRepository, adminRepository)
    }
    
    @dagger.hilt.EntryPoint
    @dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
    interface DataInitializerEntryPoint {
        fun dataInitializer(): com.realtorsuccessiq.data.repository.DataInitializer
    }
}

