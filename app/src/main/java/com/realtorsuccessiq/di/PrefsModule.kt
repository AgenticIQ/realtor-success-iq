package com.realtorsuccessiq.di

import android.content.Context
import com.realtorsuccessiq.prefs.AppPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefsModule {
    @Provides
    @Singleton
    fun provideAppPrefs(@ApplicationContext context: Context): AppPrefs = AppPrefs(context)
}

