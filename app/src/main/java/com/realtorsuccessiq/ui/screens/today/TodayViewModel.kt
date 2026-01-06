package com.realtorsuccessiq.ui.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.ActivityType
import com.realtorsuccessiq.data.model.UserSettings
import com.realtorsuccessiq.data.repository.CrmRepository
import com.realtorsuccessiq.data.repository.LocalRepository
import com.realtorsuccessiq.util.GamificationEngine
import com.realtorsuccessiq.util.Suggestion
import com.realtorsuccessiq.util.SuggestionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val crmRepository: CrmRepository,
    private val suggestionEngine: SuggestionEngine,
    private val gamificationEngine: GamificationEngine
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    private val timerStartTime = MutableStateFlow<Long?>(null)
    
    init {
        viewModelScope.launch {
            // Load settings
            localRepository.getSettings().collect { settings ->
                settings?.let {
                    _uiState.value = _uiState.value.copy(
                        targetLeadGenMinutes = it.dailyLeadGenMinutes,
                        targetCalls = it.dailyCallsTarget,
                        targetTexts = it.dailyTextsTarget,
                        targetAsks = it.dailyAppointmentAsksTarget
                    )
                }
            }
        }
        
        viewModelScope.launch {
            // Load today's activities periodically
            while (true) {
                val now = System.currentTimeMillis()
                val dayStart = now - (now % (24 * 60 * 60 * 1000L))
                val dayEnd = dayStart + 24 * 60 * 60 * 1000L
                
                val logs = localRepository.getLogsInRange(dayStart, dayEnd)
                val calls = logs.count { it.type == ActivityType.CALL_ATTEMPT || it.type == ActivityType.CONVERSATION }
                val texts = logs.count { it.type == ActivityType.TEXT_SENT }
                val asks = logs.count { it.type == ActivityType.APPT_ASK }
                
                _uiState.value = _uiState.value.copy(
                    dailyCalls = calls,
                    dailyTexts = texts,
                    dailyAsks = asks
                )
                
                delay(5000) // Update every 5 seconds
            }
        }
        
        viewModelScope.launch {
            // Keep suggestions fresh as contacts/settings change (including focus filters).
            combine(
                localRepository.getAllContacts(),
                localRepository.getSettings()
            ) { _, _ ->
                // Recompute suggestions whenever either changes.
                Unit
            }
                .debounce(300)
                .collect {
                    val suggestions = suggestionEngine.getTopSuggestions(10)
                    _uiState.value = _uiState.value.copy(suggestions = suggestions)
                }
        }

        viewModelScope.launch {
            // Keep streaks fresh
            while (true) {
                val streaks = gamificationEngine.calculateStreaks()
                _uiState.value = _uiState.value.copy(
                    leadGenStreak = streaks.leadGenStreak,
                    callStreak = streaks.callStreak
                )
                delay(5000)
            }
        }
        
        // Timer
        viewModelScope.launch {
            timerStartTime.collect { startTime ->
                if (startTime != null) {
                    while (true) {
                        delay(1000)
                        val elapsed = (System.currentTimeMillis() - startTime) / 1000
                        _uiState.value = _uiState.value.copy(
                            leadGenElapsedSeconds = elapsed,
                            isTimerRunning = true
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isTimerRunning = false)
                }
            }
        }
    }
    
    fun startTimer() {
        timerStartTime.value = System.currentTimeMillis() - (_uiState.value.leadGenElapsedSeconds * 1000)
    }
    
    fun pauseTimer() {
        timerStartTime.value = null
    }
    
    fun stopTimer() {
        val elapsed = _uiState.value.leadGenElapsedSeconds
        timerStartTime.value = null
        if (elapsed > 0) {
            viewModelScope.launch {
                localRepository.logActivity(
                    type = ActivityType.LEAD_GEN_SESSION,
                    durationSeconds = elapsed.toInt(),
                    starsAwarded = 0
                )
            }
            _uiState.value = _uiState.value.copy(leadGenElapsedSeconds = 0)
        }
    }
    
    fun logCall(personId: String) {
        viewModelScope.launch {
            val stars = gamificationEngine.getStarsForActivity(ActivityType.CALL_ATTEMPT)
            localRepository.logActivity(
                type = ActivityType.CALL_ATTEMPT,
                personId = personId,
                starsAwarded = stars
            )
            _uiState.value = _uiState.value.copy(dailyCalls = _uiState.value.dailyCalls + 1)
        }
    }
    
    fun logText(personId: String) {
        viewModelScope.launch {
            localRepository.logActivity(
                type = ActivityType.TEXT_SENT,
                personId = personId,
                starsAwarded = 0
            )
            _uiState.value = _uiState.value.copy(dailyTexts = _uiState.value.dailyTexts + 1)
        }
    }
    
    fun logConversation(personId: String) {
        viewModelScope.launch {
            val stars = gamificationEngine.getStarsForActivity(ActivityType.CONVERSATION)
            localRepository.logActivity(
                type = ActivityType.CONVERSATION,
                personId = personId,
                starsAwarded = stars
            )
            _uiState.value = _uiState.value.copy(dailyCalls = _uiState.value.dailyCalls + 1)
        }
    }
}

data class TodayUiState(
    val leadGenElapsedSeconds: Long = 0,
    val isTimerRunning: Boolean = false,
    val targetLeadGenMinutes: Int = 120,
    val dailyCalls: Int = 0,
    val dailyTexts: Int = 0,
    val dailyAsks: Int = 0,
    val targetCalls: Int = 10,
    val targetTexts: Int = 10,
    val targetAsks: Int = 1,
    val suggestions: List<Suggestion> = emptyList(),
    val leadGenStreak: Int = 0,
    val callStreak: Int = 0
)

