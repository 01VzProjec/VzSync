package com.vztechcore.vzsync.data.repos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepoDto(
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String? = null,
    @SerialName("private") val isPrivate: Boolean = false,
    @SerialName("html_url") val htmlUrl: String,
    val language: String? = null,
    @SerialName("stargazers_count") val stars: Int = 0,
    val forks: Int = 0,
    @SerialName("watchers_count") val watchers: Int = 0,
    @SerialName("updated_at") val updatedAtIso: String,
    val size: Int = 0,
    @SerialName("default_branch") val defaultBranch: String = "main",
    @SerialName("open_issues_count") val openIssues: Int = 0,
)

@Serializable
data class GitHubTreeDto(
    val sha: String = "",
    val tree: List<GitHubTreeItemDto> = emptyList(),
    val truncated: Boolean = false,
)

@Serializable
data class GitHubTreeItemDto(
    val path: String = "",
    val mode: String? = null,
    val type: String = "blob",
    val size: Int? = null,
    val sha: String? = null,
)