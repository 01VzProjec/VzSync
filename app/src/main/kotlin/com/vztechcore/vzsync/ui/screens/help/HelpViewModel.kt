package com.vztechcore.vzsync.ui.screens.help

import androidx.lifecycle.ViewModel
import com.vztechcore.vzsync.domain.help.HelpSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HelpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HelpUiState())
    val uiState: StateFlow<HelpUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun toggleSection(section: HelpSection) {
        _uiState.update {
            val set = it.expandedSections.toMutableSet()
            if (section in set) set.remove(section) else set.add(section)
            it.copy(expandedSections = set)
        }
    }

    fun focusOn(section: HelpSection) {
        _uiState.update {
            it.copy(
                query = "",
                expandedSections = it.expandedSections + section,
                autoScrollTo = section,
            )
        }
    }

    fun consumeAutoScroll() {
        _uiState.update { it.copy(autoScrollTo = null) }
    }

    fun reset() {
        _uiState.value = HelpUiState()
    }
}