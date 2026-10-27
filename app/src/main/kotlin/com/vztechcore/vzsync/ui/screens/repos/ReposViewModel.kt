package com.vztechcore.vzsync.ui.screens.repos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.data.repos.DeleteResult
import com.vztechcore.vzsync.data.repos.FileCountResult
import com.vztechcore.vzsync.data.repos.RepoRepository
import com.vztechcore.vzsync.data.repos.ReposResult
import com.vztechcore.vzsync.domain.Repo
import com.vztechcore.vzsync.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReposViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RepoRepository()

    private val _uiState = MutableStateFlow(ReposUiState())
    val uiState: StateFlow<ReposUiState> = _uiState.asStateFlow()

    private var lastToken: String = ""

    fun loadRepos(token: String, profile: UserProfile?) {
        if (_uiState.value.isLoading) return
        lastToken = token
        _uiState.update {
            it.copy(
                profile = profile ?: it.profile,
                isLoading = true,
                error = null,
            )
        }
        viewModelScope.launch {
            when (val result = repository.fetchAllRepos(token)) {
                is ReposResult.Success -> _uiState.update {
                    it.copy(isLoading = false, repos = result.repos)
                }
                is ReposResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun refresh() {
        if (lastToken.isEmpty()) return
        _uiState.update {
            it.copy(
                repos = emptyList(),
                error = null,
                fileCounts = emptyMap(),
                fileCountErrors = emptyMap(),
            )
        }
        loadRepos(lastToken, _uiState.value.profile)
    }

    fun setFilter(filter: RepoFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun onRepoClick(repo: Repo) {
        _uiState.update {
            it.copy(selectedRepo = repo, deleteError = null)
        }
        if (!_uiState.value.fileCounts.containsKey(repo.fullName) && lastToken.isNotEmpty()) {
            fetchFileCount(repo)
        }
    }

    fun dismissRepoSheet() {
        _uiState.update {
            it.copy(
                selectedRepo = null,
                showDeleteConfirm = false,
                deleteError = null,
                isDeleting = false,
            )
        }
    }

    fun requestDelete() {
        _uiState.update { it.copy(showDeleteConfirm = true, deleteError = null) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(showDeleteConfirm = false, deleteError = null) }
    }

    fun confirmDelete() {
        val repo = _uiState.value.selectedRepo ?: return
        if (lastToken.isEmpty()) return
        if (_uiState.value.isDeleting) return

        _uiState.update { it.copy(isDeleting = true, deleteError = null) }

        viewModelScope.launch {
            when (val result = repository.deleteRepo(lastToken, repo)) {
                DeleteResult.Success -> {
                    _uiState.update {
                        it.copy(
                            repos = it.repos.filterNot { r -> r.fullName == repo.fullName },
                            selectedRepo = null,
                            showDeleteConfirm = false,
                            isDeleting = false,
                            deleteError = null,
                            feedbackMessage = getApplication<Application>().getString(
                                R.string.repos_deleted_snackbar,
                                repo.name,
                            ),
                        )
                    }
                }
                is DeleteResult.Failure -> _uiState.update {
                    it.copy(
                        isDeleting = false,
                        deleteError = result.message,
                    )
                }
            }
        }
    }

    fun consumeFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    fun clear() {
        lastToken = ""
        _uiState.value = ReposUiState()
    }

    private fun fetchFileCount(repo: Repo) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(fileCounts = it.fileCounts + (repo.fullName to null))
            }
            when (val result = repository.fetchFileCount(lastToken, repo)) {
                is FileCountResult.Success -> _uiState.update {
                    it.copy(
                        fileCounts = it.fileCounts + (repo.fullName to result.count),
                    )
                }
                is FileCountResult.Failure -> _uiState.update {
                    it.copy(
                        fileCountErrors = it.fileCountErrors + (repo.fullName to result.message),
                    )
                }
            }
        }
    }
}