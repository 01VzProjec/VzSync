package com.vztechcore.vzsync.data.git

data class CloneProgress(
    val repoName: String,
    val percentage: Int,
    val phase: String,
    val isIndeterminate: Boolean = false,
)