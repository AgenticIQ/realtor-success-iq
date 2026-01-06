package com.realtorsuccessiq.ui.screens.plan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.UserSettings
import com.realtorsuccessiq.data.repository.LocalRepository
import com.realtorsuccessiq.prefs.AppPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val prefs: AppPrefs
) : ViewModel() {

    private val _uiState = mutableStateOf(PlanUiState())
    val uiState: State<PlanUiState> = _uiState

    init {
        viewModelScope.launch {
            val settings = localRepository.getSettings().first() ?: UserSettings()
            val textsCount = prefs.textsCountAsConversations.first()
            val logo = prefs.userLogoText.first()
            _uiState.value = _uiState.value.copy(
                dailyLeadGenMinutes = settings.dailyLeadGenMinutes,
                dailyCallsTarget = settings.dailyCallsTarget,
                dailyTextsTarget = settings.dailyTextsTarget,
                dailyAppointmentAsksTarget = settings.dailyAppointmentAsksTarget,
                textsCountAsConversations = textsCount,
                userLogoText = logo
            )
        }
    }

    fun setDailyLeadGenMinutes(value: Int) = updateSettings { it.copy(dailyLeadGenMinutes = value) }
    fun setDailyCallsTarget(value: Int) = updateSettings { it.copy(dailyCallsTarget = value) }
    fun setDailyTextsTarget(value: Int) = updateSettings { it.copy(dailyTextsTarget = value) }
    fun setDailyAppointmentAsksTarget(value: Int) = updateSettings { it.copy(dailyAppointmentAsksTarget = value) }

    private fun updateSettings(transform: (UserSettings) -> UserSettings) {
        viewModelScope.launch {
            val current = localRepository.getSettingsSync() ?: UserSettings()
            val updated = transform(current)
            localRepository.saveSettings(updated)
            _uiState.value = _uiState.value.copy(
                dailyLeadGenMinutes = updated.dailyLeadGenMinutes,
                dailyCallsTarget = updated.dailyCallsTarget,
                dailyTextsTarget = updated.dailyTextsTarget,
                dailyAppointmentAsksTarget = updated.dailyAppointmentAsksTarget
            )
        }
    }

    fun setTextsCountAsConversations(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setTextsCountAsConversations(enabled)
            _uiState.value = _uiState.value.copy(textsCountAsConversations = enabled)
        }
    }

    fun setUserLogoText(text: String) {
        viewModelScope.launch {
            prefs.setUserLogoText(text)
            _uiState.value = _uiState.value.copy(userLogoText = text)
        }
    }
}

data class PlanUiState(
    val dailyLeadGenMinutes: Int = 120,
    val dailyCallsTarget: Int = 10,
    val dailyTextsTarget: Int = 10,
    val dailyAppointmentAsksTarget: Int = 1,
    val textsCountAsConversations: Boolean = false,
    val userLogoText: String = ""
)


