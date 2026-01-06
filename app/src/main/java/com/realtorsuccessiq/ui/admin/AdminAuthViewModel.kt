package com.realtorsuccessiq.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.model.Brokerage
import com.realtorsuccessiq.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminAuthViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AdminAuthState>(AdminAuthState.Unauthenticated)
    val authState: StateFlow<AdminAuthState> = _authState.asStateFlow()
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AdminAuthState.Loading
            try {
                // In MVP, we'll check if brokerage exists with this email
                // In production, this would use Firebase Auth or similar
                val brokerage = adminRepository.getBrokerageByEmail(email)
                if (brokerage != null) {
                    // For MVP, accept any password. In production, verify password hash
                    _authState.value = AdminAuthState.Authenticated(brokerage.id)
                } else {
                    _authState.value = AdminAuthState.Error("No brokerage found with this email")
                }
            } catch (e: Exception) {
                _authState.value = AdminAuthState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    fun registerBrokerage(
        brokerageName: String,
        adminEmail: String,
        password: String,
        phone: String?,
        address: String?
    ) {
        viewModelScope.launch {
            _authState.value = AdminAuthState.Loading
            try {
                val existing = adminRepository.getBrokerageByEmail(adminEmail)
                if (existing != null) {
                    _authState.value = AdminAuthState.Error("A brokerage with this admin email already exists")
                    return@launch
                }

                // MVP: password is not stored/validated yet (local-only demo admin).
                @Suppress("UNUSED_VARIABLE")
                val ignored = password

                val brokerage = Brokerage(
                    id = UUID.randomUUID().toString(),
                    name = brokerageName.trim(),
                    adminEmail = adminEmail.trim(),
                    phone = phone?.trim()?.takeIf { it.isNotBlank() },
                    address = address?.trim()?.takeIf { it.isNotBlank() }
                )
                adminRepository.saveBrokerage(brokerage)
                _authState.value = AdminAuthState.Authenticated(brokerage.id)
            } catch (e: Exception) {
                _authState.value = AdminAuthState.Error(e.message ?: "Registration failed")
            }
        }
    }
    
    fun signOut() {
        _authState.value = AdminAuthState.Unauthenticated
    }
}

sealed class AdminAuthState {
    object Unauthenticated : AdminAuthState()
    object Loading : AdminAuthState()
    data class Authenticated(val brokerageId: String) : AdminAuthState()
    data class Error(val message: String) : AdminAuthState()
}

