package com.realtorsuccessiq.ui.screens.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.ActivityLog
import com.realtorsuccessiq.data.repository.LocalRepository
import com.realtorsuccessiq.util.GamificationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val gamificationEngine: GamificationEngine
) : ViewModel() {
    
    val uiState: StateFlow<ScoreUiState> = combine(
        localRepository.getAllLogs(),
        localRepository.getAllRewards(),
        flow {
            while (true) {
                emit(gamificationEngine.calculateStreaks())
                kotlinx.coroutines.delay(5000)
            }
        }
    ) { logs, rewards, streaks ->
        val totalStars = logs.sumOf { it.starsAwarded.toLong() }.toInt()
        ScoreUiState(
            totalStars = totalStars,
            leadGenStreak = streaks.leadGenStreak,
            callStreak = streaks.callStreak,
            badges = rewards
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScoreUiState()
    )
}

data class ScoreUiState(
    val totalStars: Int = 0,
    val leadGenStreak: Int = 0,
    val callStreak: Int = 0,
    val badges: List<com.realtorsuccessiq.data.model.Reward> = emptyList()
)

