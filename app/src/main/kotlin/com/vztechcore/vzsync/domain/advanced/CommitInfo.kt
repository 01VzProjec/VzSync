package com.vztechcore.vzsync.domain.advanced

import kotlinx.serialization.Serializable

@Serializable
enum class CommitMode {
    INITIAL,
    SYNC,
    REBUILD,
}

@Serializable
data class CommitInfo(
    val hash: String,
    val message: String,
    val repoFullName: String,
    val branch: String,
    val timestamp: Long,
    val filesChanged: Int = -1,
    val mode: CommitMode = CommitMode.SYNC,
    val folderDisplayPath: String = "",
)