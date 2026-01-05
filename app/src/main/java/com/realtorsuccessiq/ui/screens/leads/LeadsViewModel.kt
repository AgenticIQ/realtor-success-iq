package com.realtorsuccessiq.ui.screens.leads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtorsuccessiq.data.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadsViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _leads = MutableStateFlow<List<com.realtorsuccessiq.data.model.Contact>>(emptyList())
    val leads: StateFlow<List<com.realtorsuccessiq.data.model.Contact>> = _leads.asStateFlow()
    
    val uiState: StateFlow<LeadsUiState> = combine(
        _searchQuery,
        _leads
    ) { query, leads ->
        LeadsUiState(
            searchQuery = query,
            leads = if (query.isBlank()) leads else leads.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phone?.contains(query, ignoreCase = true) == true ||
                it.email?.contains(query, ignoreCase = true) == true
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LeadsUiState()
    )
    
    init {
        viewModelScope.launch {
            localRepository.getAllContacts().collect { contacts ->
                _leads.value = contacts
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

data class LeadsUiState(
    val searchQuery: String = "",
    val leads: List<com.realtorsuccessiq.data.model.Contact> = emptyList()
)

