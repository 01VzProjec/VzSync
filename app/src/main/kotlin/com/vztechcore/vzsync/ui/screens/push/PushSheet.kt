package com.vztechcore.vzsync.ui.screens.push

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.domain.Repo
import com.vztechcore.vzsync.ui.screens.push.steps.PushConfirmRepoStep
import com.vztechcore.vzsync.ui.screens.push.steps.PushCreateRepoStep
import com.vztechcore.vzsync.ui.screens.push.steps.PushErrorStep
import com.vztechcore.vzsync.ui.screens.push.steps.PushPushingStep
import com.vztechcore.vzsync.ui.screens.push.steps.PushSelectActionStep
import com.vztechcore.vzsync.ui.screens.push.steps.PushSelectRepoStep
import com.vztechcore.vzsync.ui.screens.push.steps.PushSuccessStep
import com.vztechcore.vzsync.domain.push.PushMode
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushSheet(
    state: PushUiState,
    repos: List<Repo>,
    isLoadingRepos: Boolean,
    reposError: String?,
    onDismiss: () -> Unit,
    onCreateSelected: () -> Unit,
    onUpdateSelected: () -> Unit,
    onNameChange: (String) -> Unit,
    onVisibilityChange: (Boolean) -> Unit,
    onRepoSelected: (Repo) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onRetryLoadRepos: () -> Unit,
    onModeChange: (PushMode) -> Unit,
    onHelpRequested: () -> Unit,
    onOpenStorageSettings: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VzSurface,
        contentColor = VzOnSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            when (state.step) {
                PushStep.SELECT_ACTION -> PushSelectActionStep(
                    onCreate = onCreateSelected,
                    onUpdate = onUpdateSelected,
                    onHelpRequested = onHelpRequested,
                )
                PushStep.CREATE_REPO -> PushCreateRepoStep(
                    state = state,
                    onNameChange = onNameChange,
                    onVisibilityChange = onVisibilityChange,
                    onBack = onBack,
                    onConfirm = onConfirm,
                )
                PushStep.SELECT_REPO -> PushSelectRepoStep(
                    repos = repos,
                    isLoading = isLoadingRepos,
                    error = reposError,
                    onBack = onBack,
                    onSelect = onRepoSelected,
                    onRetry = onRetryLoadRepos,
                )
                PushStep.CONFIRM_REPO -> PushConfirmRepoStep(
                    repo = state.selectedRepo,
                    pushMode = state.pushMode,
                    onBack = onBack,
                    onModeChange = onModeChange,
                    onConfirm = onConfirm,
                )
                PushStep.PUSHING -> PushPushingStep(
                    logs = state.logs,
                    logStyle = state.logStyle,
                    folderPath = state.folderPath,
                )
                PushStep.SUCCESS -> PushSuccessStep(
                    url = state.resultUrl.orEmpty(),
                    repoName = state.resultRepoName.orEmpty(),
                    commitHash = state.resultCommitHash,
                    filesChanged = state.resultFilesChanged,
                    logs = state.logs,
                    logStyle = state.logStyle,
                    folderPath = state.folderPath,
                    autoShowLog = state.autoShowLog,
                    postPushAction = state.postPushAction,
                    onClose = onDismiss,
                )
                PushStep.ERROR -> PushErrorStep(
                    errorMessage = state.errorMessage.orEmpty(),
                    errorReason = state.errorReason,
                    errorTechnicalDetail = state.errorTechnicalDetail,
                    logs = state.logs,
                    logStyle = state.logStyle,
                    folderPath = state.folderPath,
                    onRetry = onConfirm,
                    onOpenStorageSettings = onOpenStorageSettings,
                    onClose = onDismiss,
                )
            }
        }
    }
}