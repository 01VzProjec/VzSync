package com.vztechcore.vzsync.ui.screens.advanced

import com.vztechcore.vzsync.domain.advanced.CommitInfo

sealed interface PendingConfirmation {
    data object UndoLastCommit : PendingConfirmation
    data object DiscardChanges : PendingConfirmation
}

data class AdvancedActionsUiState(
    val allCommits: List<CommitInfo> = emptyList(),
    val availableRepos: List<String> = emptyList(),
    val selectedRepo: String? = null,
    val currentBranch: String = "main",
    val pendingConfirmation: PendingConfirmation? = null,
    val lastCommitSheet: CommitInfo? = null,
    val showBranchSheet: Boolean = false,
    val feedbackMessage: String? = null,
) {
    val commits: List<CommitInfo>
        get() = selectedRepo
            ?.let { repo -> allCommits.filter { it.repoFullName == repo } }
            ?: emptyList()

    val lastCommit: CommitInfo? get() = commits.firstOrNull()
    val hasCommits: Boolean get() = commits.isNotEmpty()
    val hasAnyRepo: Boolean get() = availableRepos.isNotEmpty()
    val hasMultipleRepos: Boolean get() = availableRepos.size > 1
}