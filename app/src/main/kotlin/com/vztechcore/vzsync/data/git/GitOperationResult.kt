package com.vztechcore.vzsync.data.git

sealed interface GitOperationResult {
    data class Success(
        val commitHash: String,
        val filesChanged: Int,
        val repoUrl: String,
        val wasInitial: Boolean = false,
    ) : GitOperationResult

    data class Failure(
        val message: String,
        val reason: FailureReason,
    ) : GitOperationResult
}

enum class FailureReason {
    STORAGE_PERMISSION_MISSING,
    FOLDER_NOT_FOUND,
    NOT_A_DIRECTORY,
    NO_TOKEN,
    REPO_OPEN_FAILED,
    REPO_INIT_FAILED,
    REMOTE_FAILED,
    ADD_FAILED,
    COMMIT_FAILED,
    PUSH_FAILED,
    UNKNOWN,
}