package com.vztechcore.vzsync.ui.screens.push

import com.vztechcore.vzsync.data.git.FailureReason
import com.vztechcore.vzsync.domain.LogEntry
import com.vztechcore.vzsync.domain.Repo
import com.vztechcore.vzsync.domain.push.LogStyle
import com.vztechcore.vzsync.domain.push.PostPushAction
import com.vztechcore.vzsync.domain.push.PushMode
import com.vztechcore.vzsync.domain.push.RepoNameSanitizer

enum class PushStep {
    SELECT_ACTION,
    CREATE_REPO,
    SELECT_REPO,
    CONFIRM_REPO,
    PUSHING,
    SUCCESS,
    ERROR,
}

data class PushUiState(
    val step: PushStep = PushStep.SELECT_ACTION,
    val repoNameInput: String = "",
    val isPrivate: Boolean = false,
    val selectedRepo: Repo? = null,
    val pushMode: PushMode = PushMode.SYNC,
    val logs: List<LogEntry> = emptyList(),
    val resultUrl: String? = null,
    val resultRepoName: String? = null,
    val errorMessage: String? = null,
    val errorReason: FailureReason? = null,
    val folderPath: String = "",
    val logStyle: LogStyle = LogStyle.TERMINAL,
    val autoShowLog: Boolean = true,
    val postPushAction: PostPushAction = PostPushAction.COMMIT_AND_PUSH,
) {
    val sanitizedName: String get() = RepoNameSanitizer.sanitize(repoNameInput)
    val isSanitizedNameValid: Boolean get() = RepoNameSanitizer.isValid(sanitizedName)
}