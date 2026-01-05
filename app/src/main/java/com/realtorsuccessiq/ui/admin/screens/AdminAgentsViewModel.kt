package com.realtorsuccessiq.ui.admin.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.Agent
import com.realtorsuccessiq.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminAgentsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AdminAgentsUiState())
    val uiState: StateFlow<AdminAgentsUiState> = _uiState.asStateFlow()
    
    fun loadAgents(brokerageId: String) {
        viewModelScope.launch {
            adminRepository.getAgents(brokerageId).collect { agents ->
                _uiState.value = _uiState.value.copy(agents = agents)
            }
        }
    }
    
    fun addAgent(brokerageId: String, name: String, email: String, phone: String?) {
        viewModelScope.launch {
            val agent = Agent(
                id = UUID.randomUUID().toString(),
                brokerageId = brokerageId,
                email = email,
                name = name,
                phone = phone,
                isActive = true
            )
            adminRepository.addAgent(agent)
        }
    }
    
    fun deactivateAgent(agentId: String) {
        viewModelScope.launch {
            adminRepository.deactivateAgent(agentId)
        }
    }
}

data class AdminAgentsUiState(
    val agents: List<Agent> = emptyList()
)

