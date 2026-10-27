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
        // Raw exception detail (javaClass + message) for diagnostics.
        // Surfaced in the error step's collapsible "technical details" box.
        val technicalDetail: String? = null,
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
    // Push-specific reasons. Split from a generic PUSH_FAILED so the UI
    // can show contextual hints (e.g., reconnect account on auth failure).
    PUSH_AUTH_FAILED,
    PUSH_NETWORK_FAILED,
    PUSH_REJECTED,
    PUSH_FAILED,
    UNKNOWN,
}