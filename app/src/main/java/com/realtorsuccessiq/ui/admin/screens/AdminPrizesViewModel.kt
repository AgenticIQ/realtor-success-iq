package com.realtorsuccessiq.ui.admin.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.Prize
import com.realtorsuccessiq.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminPrizesViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AdminPrizesUiState())
    val uiState: StateFlow<AdminPrizesUiState> = _uiState.asStateFlow()
    
    fun loadPrizes(brokerageId: String) {
        viewModelScope.launch {
            adminRepository.getAllPrizes(brokerageId).collect { prizes ->
                _uiState.value = _uiState.value.copy(prizes = prizes)
            }
        }
    }
    
    fun createPrize(brokerageId: String, prize: Prize) {
        viewModelScope.launch {
            val updatedPrize = prize.copy(brokerageId = brokerageId)
            adminRepository.createPrize(updatedPrize)
        }
    }
    
    fun deactivatePrize(prizeId: String) {
        viewModelScope.launch {
            adminRepository.deactivatePrize(prizeId)
        }
    }
}

data class AdminPrizesUiState(
    val prizes: List<Prize> = emptyList()
)

