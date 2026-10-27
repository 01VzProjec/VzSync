package com.vztechcore.vzsync.ui.screens.repos

import com.vztechcore.vzsync.domain.Repo
import com.vztechcore.vzsync.domain.UserProfile

enum class RepoFilter { All, Public, Private }

data class ReposUiState(
    val profile: UserProfile? = null,
    val repos: List<Repo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: RepoFilter = RepoFilter.All,
    val selectedRepo: Repo? = null,
    val fileCounts: Map<String, Int?> = emptyMap(),
    val fileCountErrors: Map<String, String> = emptyMap(),
    val showDeleteConfirm: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    val feedbackMessage: String? = null,
) {
    val totalRepos: Int get() = repos.size
    val publicCount: Int get() = repos.count { !it.isPrivate }
    val privateCount: Int get() = repos.count { it.isPrivate }
    val totalStars: Int get() = repos.sumOf { it.stars }

    val filtered: List<Repo> get() = when (filter) {
        RepoFilter.All -> repos
        RepoFilter.Public -> repos.filter { !it.isPrivate }
        RepoFilter.Private -> repos.filter { it.isPrivate }
    }
}