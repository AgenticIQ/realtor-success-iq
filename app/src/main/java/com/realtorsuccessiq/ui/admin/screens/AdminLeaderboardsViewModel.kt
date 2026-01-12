package com.realtorsuccessiq.ui.admin.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.LeaderboardEntry
import com.realtorsuccessiq.data.model.LeaderboardMetric
import com.realtorsuccessiq.data.model.LeaderboardPeriod
import com.realtorsuccessiq.data.repository.AdminRepository
import com.realtorsuccessiq.util.LeaderboardCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminLeaderboardsViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val leaderboardCalculator: LeaderboardCalculator
) : ViewModel() {
    
    private val _selectedPeriod = MutableStateFlow(LeaderboardPeriod.WEEKLY)
    private val _selectedMetric = MutableStateFlow(LeaderboardMetric.TOTAL_CALLS)
    
    val uiState: StateFlow<AdminLeaderboardsUiState> = combine(
        _selectedPeriod,
        _selectedMetric,
        flow {
            while (true) {
                val brokerageId = "" // Will be set via loadLeaderboard
                emit(loadLeaderboardData(brokerageId, _selectedPeriod.value, _selectedMetric.value))
                kotlinx.coroutines.delay(5000)
            }
        }
    ) { period, metric, data ->
        AdminLeaderboardsUiState(
            selectedPeriod = period,
            selectedMetric = metric,
            entries = data.entries,
            agentNames = data.agentNames
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminLeaderboardsUiState()
    )
    
    private var currentBrokerageId = ""
    
    fun loadLeaderboard(brokerageId: String) {
        currentBrokerageId = brokerageId
        refreshLeaderboard(brokerageId)
    }
    
    fun selectPeriod(period: LeaderboardPeriod) {
        _selectedPeriod.value = period
        if (currentBrokerageId.isNotBlank()) {
            refreshLeaderboard(currentBrokerageId)
        }
    }
    
    fun selectMetric(metric: LeaderboardMetric) {
        _selectedMetric.value = metric
        if (currentBrokerageId.isNotBlank()) {
            refreshLeaderboard(currentBrokerageId)
        }
    }
    
    fun refreshLeaderboard(brokerageId: String) {
        viewModelScope.launch {
            // Recalculate leaderboard
            leaderboardCalculator.calculateLeaderboard(
                brokerageId,
                _selectedPeriod.value,
                _selectedMetric.value
            )
        }
    }
    
    private suspend fun loadLeaderboardData(
        brokerageId: String,
        period: LeaderboardPeriod,
        metric: LeaderboardMetric
    ): LeaderboardData {
        val entries = adminRepository.getLeaderboard(brokerageId, period, metric)
        val agents = adminRepository.getAllAgents(brokerageId)
        val agentNames = agents.associateBy({ it.id }, { it.name })
        
        return LeaderboardData(entries, agentNames)
    }
}

data class AdminLeaderboardsUiState(
    val selectedPeriod: LeaderboardPeriod = LeaderboardPeriod.WEEKLY,
    val selectedMetric: LeaderboardMetric = LeaderboardMetric.TOTAL_CALLS,
    val entries: List<LeaderboardEntry> = emptyList(),
    val agentNames: Map<String, String> = emptyMap()
)

private data class LeaderboardData(
    val entries: List<LeaderboardEntry>,
    val agentNames: Map<String, String>
)
