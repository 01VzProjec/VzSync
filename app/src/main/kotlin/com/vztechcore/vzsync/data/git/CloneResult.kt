package com.vztechcore.vzsync.data.git

import java.io.File

sealed interface CloneResult {
    data class Success(
        val file: File,
        val repoName: String,
        val repoFullName: String,
    ) : CloneResult

    data class Failure(
        val message: String,
        val reason: CloneFailureReason,
    ) : CloneResult
}

enum class CloneFailureReason {
    INVALID_URL,
    STORAGE_PERMISSION_MISSING,
    BASE_DIR_UNAVAILABLE,
    AUTH_FAILED,
    NETWORK_ERROR,
    REPO_NOT_FOUND,
    CLONE_FAILED,
    CANCELLED,
    UNKNOWN,
}