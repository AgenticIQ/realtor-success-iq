package com.realtorsuccessiq.ui.admin.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.LeaderboardMetric
import com.realtorsuccessiq.data.model.LeaderboardPeriod
import com.realtorsuccessiq.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()
    
    fun loadDashboard(brokerageId: String) {
        viewModelScope.launch {
            // Load active agents count
            val agents = adminRepository.getAllAgents(brokerageId)
            val activeAgents = agents.filter { it.isActive }
            
            // Load leaderboards for metrics
            val weeklyCalls = adminRepository.getLeaderboard(
                brokerageId,
                LeaderboardPeriod.WEEKLY,
                LeaderboardMetric.TOTAL_CALLS,
                limit = 5
            )
            
            val weeklyAppointments = adminRepository.getLeaderboard(
                brokerageId,
                LeaderboardPeriod.WEEKLY,
                LeaderboardMetric.APPOINTMENTS_SET,
                limit = 5
            )
            
            val weeklyListings = adminRepository.getLeaderboard(
                brokerageId,
                LeaderboardPeriod.WEEKLY,
                LeaderboardMetric.LISTINGS_SIGNED,
                limit = 5
            )
            
            // Get agent names
            val agentMap = agents.associateBy { it.id }
            
            val topPerformers = listOf(
                weeklyCalls.firstOrNull()?.let { entry ->
                    TopPerformer(
                        agentName = agentMap[entry.agentId]?.name ?: "Unknown",
                        metric = "Most Calls",
                        value = entry.value
                    )
                },
                weeklyAppointments.firstOrNull()?.let { entry ->
                    TopPerformer(
                        agentName = agentMap[entry.agentId]?.name ?: "Unknown",
                        metric = "Most Appointments",
                        value = entry.value
                    )
                },
                weeklyListings.firstOrNull()?.let { entry ->
                    TopPerformer(
                        agentName = agentMap[entry.agentId]?.name ?: "Unknown",
                        metric = "Most Listings",
                        value = entry.value
                    )
                }
            ).filterNotNull()
            
            val totalCalls = weeklyCalls.sumOf { it.value }
            val totalAppointments = weeklyAppointments.sumOf { it.value }
            val totalListings = weeklyListings.sumOf { it.value }
            
            _uiState.value = AdminDashboardUiState(
                activeAgentsCount = activeAgents.size,
                totalCalls = totalCalls,
                totalAppointments = totalAppointments,
                totalListings = totalListings,
                topPerformers = topPerformers
            )
        }
    }
}

data class AdminDashboardUiState(
    val activeAgentsCount: Int = 0,
    val totalCalls: Int = 0,
    val totalAppointments: Int = 0,
    val totalListings: Int = 0,
    val topPerformers: List<TopPerformer> = emptyList()
)

