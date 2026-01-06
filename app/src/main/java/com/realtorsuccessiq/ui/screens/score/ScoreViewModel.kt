package com.realtorsuccessiq.ui.screens.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        localRepository.getSettings(),
        localRepository.getAllContacts(),
        flow {
            while (true) {
                emit(gamificationEngine.calculateStreaks())
                kotlinx.coroutines.delay(5000)
            }
        }
    ) { logs, rewards, settings, contacts, streaks ->
        val focusTags = settings?.crmFocusTags
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()
        val focusStages = settings?.crmFocusStages
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()

        val allowedPersonIds = contacts
            .filter { c ->
                val tagOk = focusTags.isEmpty() || c.getTagsList().any { it in focusTags }
                val stageOk = focusStages.isEmpty() || (c.stage != null && focusStages.contains(c.stage))
                tagOk && stageOk
            }
            .map { it.id }
            .toSet()

        val filteredLogs = if (focusTags.isEmpty() && focusStages.isEmpty()) {
            logs
        } else {
            logs.filter { it.personId == null || (it.personId in allowedPersonIds) }
        }

        val totalStars = filteredLogs.sumOf { it.starsAwarded.toLong() }.toInt()
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

