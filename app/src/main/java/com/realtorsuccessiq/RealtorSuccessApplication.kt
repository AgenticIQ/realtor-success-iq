package com.realtorsuccessiq

import android.app.Application
import com.realtorsuccessiq.data.repository.DataInitializer
import com.realtorsuccessiq.di.RepositoryModule
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.EntryPointAccessors

@HiltAndroidApp
class RealtorSuccessApplication : Application() {
    
    private val _dataInitializer: DataInitializer by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            RepositoryModule.DataInitializerEntryPoint::class.java
        ).dataInitializer()
    }
    
    fun getDataInitializer(): DataInitializer = _dataInitializer
}

