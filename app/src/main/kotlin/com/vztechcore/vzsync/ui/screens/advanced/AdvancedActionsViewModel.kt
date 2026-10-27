package com.vztechcore.vzsync.ui.screens.advanced

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.vztechcore.vzsync.data.advanced.CommitHistoryStore
import com.vztechcore.vzsync.domain.advanced.AdvancedAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdvancedActionsViewModel(application: Application) : AndroidViewModel(application) {

    private val store = CommitHistoryStore.get(application)

    private val _uiState = MutableStateFlow(AdvancedActionsUiState())
    val uiState: StateFlow<AdvancedActionsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    /**
     * Reloads all commits and re-derives the available repos.
     * If [folderHint] is provided, tries to auto-select the repo that was last
     * used with that folder. Falls back to the most recently active repo.
     */
    fun refresh(folderHint: String? = null) {
        val all = store.getAll()
        val repos = all.map { it.repoFullName }.distinct()
        val current = _uiState.value.selectedRepo

        val autoSelected = when {
            current != null && current in repos -> current
            folderHint != null -> {
                store.getMostRecentRepoForFolder(folderHint) ?: repos.firstOrNull()
            }
            else -> repos.firstOrNull()
        }

        _uiState.update {
            it.copy(
                allCommits = all,
                availableRepos = repos,
                selectedRepo = autoSelected,
                currentBranch = store.getCurrentBranch(),
            )
        }
    }

    fun selectRepo(repoFullName: String) {
        if (repoFullName !in _uiState.value.availableRepos) return
        _uiState.update {
            it.copy(
                selectedRepo = repoFullName,
                pendingConfirmation = null,
                lastCommitSheet = null,
                showBranchSheet = false,
            )
        }
    }

    fun onActionClick(action: AdvancedAction) {
        when (action) {
            AdvancedAction.VIEW_LAST_COMMIT -> {
                val last = _uiState.value.lastCommit
                if (last == null) {
                    _uiState.update {
                        it.copy(feedbackMessage = "Nenhum commit neste repositório ainda.")
                    }
                } else {
                    _uiState.update { it.copy(lastCommitSheet = last) }
                }
            }
            AdvancedAction.UNDO_LAST_COMMIT -> {
                if (!_uiState.value.hasCommits) {
                    _uiState.update {
                        it.copy(feedbackMessage = "Nenhum commit para desfazer neste repositório.")
                    }
                } else {
                    _uiState.update {
                        it.copy(pendingConfirmation = PendingConfirmation.UndoLastCommit)
                    }
                }
            }
            AdvancedAction.DISCARD_LOCAL_CHANGES -> {
                _uiState.update {
                    it.copy(pendingConfirmation = PendingConfirmation.DiscardChanges)
                }
            }
            AdvancedAction.SWITCH_BRANCH -> {
                _uiState.update { it.copy(showBranchSheet = true) }
            }
        }
    }

    fun confirmPending() {
        when (_uiState.value.pendingConfirmation) {
            PendingConfirmation.UndoLastCommit -> {
                val repo = _uiState.value.selectedRepo
                if (repo == null) {
                    _uiState.update {
                        it.copy(
                            pendingConfirmation = null,
                            feedbackMessage = "Nenhum repositório selecionado.",
                        )
                    }
                    return
                }
                val removed = store.removeLastForRepo(repo)
                refresh()
                _uiState.update {
                    it.copy(
                        pendingConfirmation = null,
                        feedbackMessage = if (removed != null) {
                            "Último commit de '$repo' desfeito localmente."
                        } else {
                            "Nenhum commit para desfazer."
                        },
                    )
                }
            }
            PendingConfirmation.DiscardChanges -> {
                // TODO: call JGit checkout once the real pipeline is wired.
                _uiState.update {
                    it.copy(
                        pendingConfirmation = null,
                        feedbackMessage = "Nenhuma alteração local detectada.",
                    )
                }
            }
            null -> Unit
        }
    }

    fun cancelPending() {
        _uiState.update { it.copy(pendingConfirmation = null) }
    }

    fun dismissLastCommitSheet() {
        _uiState.update { it.copy(lastCommitSheet = null) }
    }

    fun dismissBranchSheet() {
        _uiState.update { it.copy(showBranchSheet = false) }
    }

    fun changeBranch(newBranch: String) {
        val trimmed = newBranch.trim()
        if (trimmed.isEmpty()) return
        // TODO: run git checkout once the real pipeline is wired.
        store.setCurrentBranch(trimmed)
        _uiState.update {
            it.copy(
                currentBranch = trimmed,
                showBranchSheet = false,
                feedbackMessage = "Branch alterado para '$trimmed'.",
            )
        }
    }

    fun consumeFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}