package com.realtorsuccessiq.data.database

import androidx.room.TypeConverter
import com.realtorsuccessiq.data.model.ActivityType
import com.realtorsuccessiq.data.model.LeaderboardMetric
import com.realtorsuccessiq.data.model.LeaderboardPeriod
import com.realtorsuccessiq.data.model.PrizeType

class Converters {
    @TypeConverter
    fun fromActivityType(value: ActivityType): String {
        return value.name
    }
    
    @TypeConverter
    fun toActivityType(value: String): ActivityType {
        return ActivityType.valueOf(value)
    }
    
    @TypeConverter
    fun fromLeaderboardPeriod(value: LeaderboardPeriod): String {
        return value.name
    }
    
    @TypeConverter
    fun toLeaderboardPeriod(value: String): LeaderboardPeriod {
        return LeaderboardPeriod.valueOf(value)
    }
    
    @TypeConverter
    fun fromLeaderboardMetric(value: LeaderboardMetric): String {
        return value.name
    }
    
    @TypeConverter
    fun toLeaderboardMetric(value: String): LeaderboardMetric {
        return LeaderboardMetric.valueOf(value)
    }
    
    @TypeConverter
    fun fromPrizeType(value: PrizeType): String {
        return value.name
    }
    
    @TypeConverter
    fun toPrizeType(value: String): PrizeType {
        return PrizeType.valueOf(value)
    }
}

