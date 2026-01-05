package com.realtorsuccessiq.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.ActivityType
import com.realtorsuccessiq.data.repository.LocalRepository
import com.realtorsuccessiq.util.GamificationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val gamificationEngine: GamificationEngine
) : ViewModel() {
    
    private val _whatWorked = MutableStateFlow("")
    private val _whatDidntWork = MutableStateFlow("")
    private val _nextWeekFocus = MutableStateFlow("")
    
    val uiState: StateFlow<ReviewUiState> = combine(
        _whatWorked,
        _whatDidntWork,
        _nextWeekFocus,
        flow {
            // Generate weekly summary
            while (true) {
                val now = System.currentTimeMillis()
                val weekStart = now - (7 * 24 * 60 * 60 * 1000L)
                val logs = localRepository.getLogsInRange(weekStart, now)
                
                val calls = logs.count { it.type == ActivityType.CALL_ATTEMPT || it.type == ActivityType.CONVERSATION }
                val conversations = logs.count { it.type == ActivityType.CONVERSATION }
                val appointments = logs.count { it.type == ActivityType.APPT_SET }
                val listingAppts = logs.count { it.type == ActivityType.LISTING_APPT }
                
                emit("This week you made $calls calls, had $conversations conversations, set $appointments appointments, and scheduled $listingAppts listing appointments.")
                kotlinx.coroutines.delay(5000)
            }
        }
    ) { worked, didnt, focus, summary ->
        ReviewUiState(
            summary = summary,
            whatWorked = worked,
            whatDidntWork = didnt,
            nextWeekFocus = focus
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReviewUiState()
    )
    
    fun updateWhatWorked(text: String) {
        _whatWorked.value = text
    }
    
    fun updateWhatDidntWork(text: String) {
        _whatDidntWork.value = text
    }
    
    fun updateNextWeekFocus(text: String) {
        _nextWeekFocus.value = text
    }
    
    fun saveReview() {
        viewModelScope.launch {
            val stars = gamificationEngine.getStarsForActivity(ActivityType.WEEKLY_REVIEW)
            localRepository.logActivity(
                type = ActivityType.WEEKLY_REVIEW,
                notes = "What worked: ${_whatWorked.value}\nWhat didn't: ${_whatDidntWork.value}\nNext week: ${_nextWeekFocus.value}",
                starsAwarded = stars
            )
        }
    }
}

data class ReviewUiState(
    val summary: String = "",
    val whatWorked: String = "",
    val whatDidntWork: String = "",
    val nextWeekFocus: String = ""
)

