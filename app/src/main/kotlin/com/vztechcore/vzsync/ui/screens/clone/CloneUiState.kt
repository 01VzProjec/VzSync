package com.vztechcore.vzsync.ui.screens.clone

import com.vztechcore.vzsync.data.git.CloneFailureReason
import com.vztechcore.vzsync.domain.LogEntry

enum class CloneStep {
    INPUT,
    CLONING,
    SUCCESS,
    ERROR,
}

data class CloneUiState(
    val step: CloneStep = CloneStep.INPUT,
    val url: String = "",
    val logs: List<LogEntry> = emptyList(),
    val percentage: Int = 0,
    val phase: String = "",
    val isIndeterminate: Boolean = false,
    val isHidden: Boolean = false,
    val showXiaomiDialog: Boolean = false,
    val resultPath: String? = null,
    val resultRepoName: String? = null,
    val errorMessage: String? = null,
    val errorReason: CloneFailureReason? = null,
) {
    val isUrlValid: Boolean get() = url.isNotBlank() && url.contains("github.com")
}