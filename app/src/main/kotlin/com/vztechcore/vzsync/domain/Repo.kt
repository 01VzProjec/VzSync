package com.vztechcore.vzsync.domain

data class Repo(
    val name: String,
    val fullName: String,
    val description: String?,
    val isPrivate: Boolean,
    val htmlUrl: String,
    val language: String?,
    val stars: Int,
    val forks: Int,
    val watchers: Int,
    val updatedAtIso: String,
    val sizeKb: Int = 0,
    val defaultBranch: String = "main",
    val openIssues: Int = 0,
)