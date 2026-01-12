package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 'default'")
    fun getSettings(): Flow<UserSettings?>
    
    @Query("SELECT * FROM user_settings WHERE id = 'default'")
    suspend fun getSettingsSync(): UserSettings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettings)
    
    @Update
    suspend fun updateSettings(settings: UserSettings)
}
