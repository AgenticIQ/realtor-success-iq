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

    private val _focusTags = MutableStateFlow<List<String>>(emptyList())
    private val _focusStages = MutableStateFlow<List<String>>(emptyList())
    
    val uiState: StateFlow<LeadsUiState> = combine(
        _searchQuery,
        _leads,
        _focusTags,
        _focusStages
    ) { query, leads, focusTags, focusStages ->
        val focusTagsNorm = focusTags.map { it.trim().lowercase() }.filter { it.isNotBlank() }.toSet()
        val focusStagesNorm = focusStages.map { it.trim().lowercase() }.filter { it.isNotBlank() }.toSet()
        val filteredByCrm = leads.filter { c ->
            val tagOk = focusTagsNorm.isEmpty() || c.getTagsList().any { it.trim().lowercase() in focusTagsNorm }
            val stageOk = focusStagesNorm.isEmpty() || (c.stage?.trim()?.lowercase() in focusStagesNorm)
            tagOk && stageOk
        }
        LeadsUiState(
            searchQuery = query,
            leads = if (query.isBlank()) filteredByCrm else filteredByCrm.filter {
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
        viewModelScope.launch {
            localRepository.getSettings().collect { settings ->
                val tags = settings?.crmFocusTags
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() }
                    .orEmpty()
                val stages = settings?.crmFocusStages
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() }
                    .orEmpty()
                _focusTags.value = tags
                _focusStages.value = stages
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

