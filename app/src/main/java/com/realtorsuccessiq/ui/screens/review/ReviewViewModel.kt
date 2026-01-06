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

    private val logsFlow: Flow<List<com.realtorsuccessiq.data.model.ActivityLog>> = flow {
        // Generate weekly logs snapshot periodically
        while (true) {
            val now = System.currentTimeMillis()
            val weekStart = now - (7 * 24 * 60 * 60 * 1000L)
            emit(localRepository.getLogsInRange(weekStart, now))
            kotlinx.coroutines.delay(5000)
        }
    }

    private val summaryFlow: Flow<String> = combine(
        localRepository.getSettings(),
        localRepository.getAllContacts(),
        logsFlow
    ) { settings, contacts, logs ->
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

        val calls = filteredLogs.count { it.type == ActivityType.CALL_ATTEMPT || it.type == ActivityType.CONVERSATION }
        val conversations = filteredLogs.count { it.type == ActivityType.CONVERSATION }
        val appointments = filteredLogs.count { it.type == ActivityType.APPT_SET }
        val listingAppts = filteredLogs.count { it.type == ActivityType.LISTING_APPT }

        "This week you made $calls calls, had $conversations conversations, set $appointments appointments, and scheduled $listingAppts listing appointments."
    }
    
    val uiState: StateFlow<ReviewUiState> = combine(
        _whatWorked,
        _whatDidntWork,
        _nextWeekFocus,
        summaryFlow
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

