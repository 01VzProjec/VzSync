package com.vztechcore.vzsync.domain

/**
 * Authenticated GitHub user, including profile stats.
 */
data class UserProfile(
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val bio: String? = null,
    val publicRepos: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val createdAt: String? = null,
)