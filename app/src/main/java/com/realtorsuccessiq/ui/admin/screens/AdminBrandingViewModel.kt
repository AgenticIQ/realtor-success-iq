package com.realtorsuccessiq.ui.admin.screens

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.Brokerage
import com.realtorsuccessiq.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminBrandingViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AdminBrandingUiState())
    val uiState: StateFlow<AdminBrandingUiState> = _uiState.asStateFlow()
    
    fun loadBrokerage(brokerageId: String) {
        viewModelScope.launch {
            adminRepository.getBrokerageFlow(brokerageId).collect { brokerage ->
                brokerage?.let {
                    _uiState.value = AdminBrandingUiState(
                        logoUrl = it.logoUrl,
                        primaryColor = it.primaryColor,
                        secondaryColor = it.secondaryColor,
                        accentColor = it.accentColor
                    )
                }
            }
        }
    }
    
    fun uploadLogo(brokerageId: String) {
        viewModelScope.launch {
            // In MVP, this would open file picker and upload to storage
            // For now, we'll use a placeholder URL
            val logoUrl = "https://via.placeholder.com/200" // Replace with actual upload
            val brokerage = adminRepository.getBrokerage(brokerageId)
            brokerage?.let {
                adminRepository.updateBrokerage(it.copy(logoUrl = logoUrl))
            }
        }
    }
    
    fun updatePrimaryColor(brokerageId: String, color: Long) {
        viewModelScope.launch {
            val brokerage = adminRepository.getBrokerage(brokerageId)
            brokerage?.let {
                adminRepository.updateBrokerage(it.copy(primaryColor = color))
            }
        }
    }
    
    fun updateSecondaryColor(brokerageId: String, color: Long) {
        viewModelScope.launch {
            val brokerage = adminRepository.getBrokerage(brokerageId)
            brokerage?.let {
                adminRepository.updateBrokerage(it.copy(secondaryColor = color))
            }
        }
    }
    
    fun updateAccentColor(brokerageId: String, color: Long) {
        viewModelScope.launch {
            val brokerage = adminRepository.getBrokerage(brokerageId)
            brokerage?.let {
                adminRepository.updateBrokerage(it.copy(accentColor = color))
            }
        }
    }
}

data class AdminBrandingUiState(
    val logoUrl: String? = null,
    val primaryColor: Long = 0xFF16A34A,
    val secondaryColor: Long = 0xFF0B1220,
    val accentColor: Long = 0xFFD4AF37
)
