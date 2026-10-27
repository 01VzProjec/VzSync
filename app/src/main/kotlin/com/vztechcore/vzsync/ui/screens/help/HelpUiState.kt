package com.vztechcore.vzsync.ui.screens.help

import com.vztechcore.vzsync.domain.help.HelpSection

data class HelpUiState(
    val query: String = "",
    val expandedSections: Set<HelpSection> = emptySet(),
    val autoScrollTo: HelpSection? = null,
) {
    val filteredSections: List<HelpSection>
        get() {
            if (query.isBlank()) return HelpSection.entries
            val q = query.trim()
            return HelpSection.entries.filter { section ->
                section.name.contains(q, ignoreCase = true) ||
                    section.keywords.any { it.contains(q, ignoreCase = true) }
            }
        }
}