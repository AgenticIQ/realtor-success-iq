package com.realtorsuccessiq.di

import com.realtorsuccessiq.data.repository.LocalRepository
import com.realtorsuccessiq.util.GamificationEngine
import com.realtorsuccessiq.util.SuggestionEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    @Provides
    @Singleton
    fun provideGamificationEngine(
        localRepository: LocalRepository
    ): GamificationEngine {
        return GamificationEngine(localRepository)
    }
    
    @Provides
    @Singleton
    fun provideSuggestionEngine(
        localRepository: LocalRepository
    ): SuggestionEngine {
        return SuggestionEngine(localRepository)
    }
    
    @Provides
    @Singleton
    fun provideLeaderboardCalculator(
        adminRepository: com.realtorsuccessiq.data.repository.AdminRepository,
        localRepository: LocalRepository
    ): com.realtorsuccessiq.util.LeaderboardCalculator {
        return com.realtorsuccessiq.util.LeaderboardCalculator(adminRepository, localRepository)
    }
}

