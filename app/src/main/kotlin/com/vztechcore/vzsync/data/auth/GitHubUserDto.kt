package com.vztechcore.vzsync.data.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUserDto(
    val login: String,
    val id: Long,
    @SerialName("avatar_url") val avatarUrl: String,
    val name: String? = null,
    val bio: String? = null,
    @SerialName("public_repos") val publicRepos: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
)