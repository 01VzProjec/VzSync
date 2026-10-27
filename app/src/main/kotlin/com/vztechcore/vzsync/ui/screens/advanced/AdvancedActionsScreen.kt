package com.vztechcore.vzsync.ui.screens.advanced

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.advanced.AdvancedAction
import com.vztechcore.vzsync.domain.advanced.CommitInfo
import com.vztechcore.vzsync.domain.advanced.CommitMode
import com.vztechcore.vzsync.ui.theme.DarkError
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurface
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Amber = Color(0xFFF59E0B)

@Composable
fun AdvancedActionsScreen(
    uiState: AdvancedActionsUiState,
    onBack: () -> Unit,
    onActionClick: (AdvancedAction) -> Unit,
    onSelectRepo: (String) -> Unit,
    onConfirmPending: () -> Unit,
    onCancelPending: () -> Unit,
    onDismissLastCommitSheet: () -> Unit,
    onDismissBranchSheet: () -> Unit,
    onChangeBranch: (String) -> Unit,
    onFeedbackConsumed: () -> Unit,
    onHelpRequested: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage) {
        val msg = uiState.feedbackMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        onFeedbackConsumed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VzBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            TopBar(onBack = onBack, onHelp = onHelpRequested)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(Modifier.height(8.dp))

                InfoCard()

                Spacer(Modifier.height(20.dp))

                if (uiState.hasMultipleRepos && uiState.selectedRepo != null) {
                    SectionHeader(stringResource(R.string.advanced_repo_label))
                    RepoSelector(
                        selectedRepo = uiState.selectedRepo,
                        availableRepos = uiState.availableRepos,
                        onSelect = onSelectRepo,
                    )
                    Spacer(Modifier.height(20.dp))
                }

                SectionHeader(stringResource(R.string.advanced_history_title))
                CommitHistorySection(uiState = uiState)

                Spacer(Modifier.height(20.dp))

                SectionHeader(stringResource(R.string.advanced_actions_section))
                ActionRow(
                    icon = Icons.Rounded.History,
                    action = AdvancedAction.VIEW_LAST_COMMIT,
                    onClick = { onActionClick(AdvancedAction.VIEW_LAST_COMMIT) },
                )
                ActionRow(
                    icon = Icons.Rounded.Undo,
                    action = AdvancedAction.UNDO_LAST_COMMIT,
                    onClick = { onActionClick(AdvancedAction.UNDO_LAST_COMMIT) },
                )
                ActionRow(
                    icon = Icons.Rounded.DeleteSweep,
                    action = AdvancedAction.DISCARD_LOCAL_CHANGES,
                    onClick = { onActionClick(AdvancedAction.DISCARD_LOCAL_CHANGES) },
                )
                ActionRow(
                    icon = Icons.Rounded.SwapHoriz,
                    action = AdvancedAction.SWITCH_BRANCH,
                    subtitleOverride = stringResource(
                        R.string.advanced_branch_current,
                        uiState.currentBranch,
                    ),
                    onClick = { onActionClick(AdvancedAction.SWITCH_BRANCH) },
                )

                Spacer(Modifier.height(24.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
        )
    }

    uiState.pendingConfirmation?.let { pending ->
        ConfirmDialog(
            pending = pending,
            onConfirm = onConfirmPending,
            onDismiss = onCancelPending,
        )
    }

    uiState.lastCommitSheet?.let { commit ->
        LastCommitSheet(
            commit = commit,
            onDismiss = onDismissLastCommitSheet,
        )
    }

    if (uiState.showBranchSheet) {
        BranchSheet(
            currentBranch = uiState.currentBranch,
            onConfirm = onChangeBranch,
            onDismiss = onDismissBranchSheet,
        )
    }
}

@Composable
private fun TopBar(onBack: () -> Unit, onHelp: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.common_back),
                tint = VzOnSurface,
            )
        }
        Text(
            text = stringResource(R.string.advanced_title),
            style = MaterialTheme.typography.titleLarge,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onHelp) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                contentDescription = stringResource(R.string.advanced_help_action),
                tint = VzOnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun InfoCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Amber.copy(alpha = 0.10f))
            .border(1.dp, Amber.copy(alpha = 0.30f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = null,
            tint = Amber,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.advanced_info_card),
            style = MaterialTheme.typography.bodySmall,
            color = Amber,
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.bodySmall,
        color = VzOnSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
    )
}

@Composable
private fun RepoSelector(
    selectedRepo: String,
    availableRepos: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, VzSurfaceVariant, RoundedCornerShape(14.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Folder,
                contentDescription = null,
                tint = DarkSecondary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.advanced_repo_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                )
                Text(
                    text = selectedRepo,
                    style = MaterialTheme.typography.titleMedium,
                    color = VzOnSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = stringResource(R.string.advanced_repo_change),
                tint = VzOnSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            availableRepos.forEach { repo ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = repo,
                            fontFamily = FontFamily.Monospace,
                        )
                    },
                    trailingIcon = if (repo == selectedRepo) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = DarkSecondary,
                            )
                        }
                    } else null,
                    onClick = {
                        onSelect(repo)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun CommitHistorySection(uiState: AdvancedActionsUiState) {
    if (!uiState.hasAnyRepo) {
        EmptyState(text = stringResource(R.string.advanced_empty_no_commits))
        return
    }

    if (uiState.selectedRepo == null) {
        EmptyState(text = stringResource(R.string.advanced_empty_no_repo))
        return
    }

    if (!uiState.hasCommits) {
        EmptyState(
            text = stringResource(R.string.advanced_empty_repo_no_commits, uiState.selectedRepo)
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        uiState.commits.forEach { commit ->
            CommitCard(commit = commit)
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, VzSurfaceVariant, RoundedCornerShape(14.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}

@Composable
private fun CommitCard(commit: CommitInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, VzSurfaceVariant, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = commit.message,
            style = MaterialTheme.typography.bodyMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = commit.branch,
                style = MaterialTheme.typography.bodySmall,
                color = DarkSecondary,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                text = "·",
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
            Text(
                text = commit.hash,
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                fontFamily = FontFamily.Monospace,
            )
            if (commit.folderDisplayPath.isNotBlank()) {
                Text(
                    text = "·",
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                )
                Text(
                    text = commit.folderDisplayPath,
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
        Text(
            text = formatTimestamp(commit.timestamp) + modeLabel(commit.mode),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    action: AdvancedAction,
    subtitleOverride: String? = null,
    onClick: () -> Unit,
) {
    val tint = if (action.isDestructive) DarkError else VzOnSurface
    val title = stringResource(advancedActionTitleRes(action))
    val subtitle = subtitleOverride ?: stringResource(advancedActionDescRes(action))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = tint,
                    fontWeight = FontWeight.SemiBold,
                )
                if (action.isDestructive) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = stringResource(R.string.advanced_destructive_badge),
                        tint = DarkError,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = VzOnSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}

private fun advancedActionTitleRes(action: AdvancedAction): Int = when (action) {
    AdvancedAction.VIEW_LAST_COMMIT -> R.string.advanced_action_view_last
    AdvancedAction.UNDO_LAST_COMMIT -> R.string.advanced_action_undo
    AdvancedAction.DISCARD_LOCAL_CHANGES -> R.string.advanced_action_discard
    AdvancedAction.SWITCH_BRANCH -> R.string.advanced_action_switch
}

private fun advancedActionDescRes(action: AdvancedAction): Int = when (action) {
    AdvancedAction.VIEW_LAST_COMMIT -> R.string.advanced_action_view_last_desc
    AdvancedAction.UNDO_LAST_COMMIT -> R.string.advanced_action_undo_desc
    AdvancedAction.DISCARD_LOCAL_CHANGES -> R.string.advanced_action_discard_desc
    AdvancedAction.SWITCH_BRANCH -> R.string.advanced_action_switch_desc
}

@Composable
private fun ConfirmDialog(
    pending: PendingConfirmation,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val titleRes: Int
    val bodyRes: Int
    when (pending) {
        PendingConfirmation.UndoLastCommit -> {
            titleRes = R.string.advanced_confirm_undo_title
            bodyRes = R.string.advanced_confirm_undo_body
        }
        PendingConfirmation.DiscardChanges -> {
            titleRes = R.string.advanced_confirm_discard_title
            bodyRes = R.string.advanced_confirm_discard_body
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VzSurface,
        titleContentColor = VzOnSurface,
        textContentColor = VzOnSurfaceVariant,
        title = {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = stringResource(bodyRes),
                style = MaterialTheme.typography.bodySmall,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.advanced_confirm),
                    color = DarkError,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.common_cancel),
                    color = VzOnSurfaceVariant,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LastCommitSheet(
    commit: CommitInfo,
    onDismiss: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.advanced_last_commit_title),
                style = MaterialTheme.typography.titleLarge,
                color = VzOnSurface,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = commit.message,
                style = MaterialTheme.typography.bodyMedium,
                color = VzOnSurface,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, VzSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                DetailRow(stringResource(R.string.advanced_detail_hash), commit.hash, mono = true)
                DetailRow(stringResource(R.string.advanced_detail_repo), commit.repoFullName, mono = true)
                DetailRow(stringResource(R.string.advanced_detail_branch), commit.branch, mono = true)
                DetailRow(stringResource(R.string.advanced_detail_date), formatTimestamp(commit.timestamp))
                DetailRow(
                    stringResource(R.string.advanced_detail_mode),
                    modeLabel(commit.mode).trimStart(' ', '·').trim(),
                )
                if (commit.folderDisplayPath.isNotBlank()) {
                    DetailRow(stringResource(R.string.advanced_detail_folder), commit.folderDisplayPath, mono = true)
                }
                if (commit.filesChanged >= 0) {
                    DetailRow(stringResource(R.string.advanced_detail_files), "${commit.filesChanged}")
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkPrimary,
                    contentColor = VzOnSurface,
                ),
            ) {
                Text(
                    text = stringResource(R.string.common_close),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchSheet(
    currentBranch: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var input by remember { mutableStateOf(currentBranch) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.advanced_branch_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                color = VzOnSurface,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.advanced_branch_current, currentBranch),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.advanced_branch_sheet_hint),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text(stringResource(R.string.advanced_branch_sheet_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions.Default,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkPrimary,
                    unfocusedBorderColor = VzSurfaceVariant,
                    focusedLabelColor = VzOnSurfaceVariant,
                    unfocusedLabelColor = VzOnSurfaceVariant,
                    cursorColor = DarkPrimary,
                    focusedTextColor = VzOnSurface,
                    unfocusedTextColor = VzOnSurface,
                ),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VzSurfaceVariant,
                        contentColor = VzOnSurface,
                    ),
                ) {
                    Text(
                        stringResource(R.string.common_cancel),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Button(
                    onClick = { onConfirm(input) },
                    enabled = input.isNotBlank() && input != currentBranch,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkPrimary,
                        contentColor = VzOnSurface,
                    ),
                ) {
                    Text(
                        stringResource(R.string.advanced_branch_sheet_change),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, mono: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
            modifier = Modifier.width(110.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurface,
            fontFamily = if (mono) FontFamily.Monospace else null,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun formatTimestamp(ms: Long): String {
    return try {
        val instant = Instant.ofEpochMilli(ms)
        val formatter = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (_: Exception) {
        ""
    }
}

@Composable
private fun modeLabel(mode: CommitMode): String = when (mode) {
    CommitMode.INITIAL -> " · " + stringResource(R.string.advanced_mode_initial)
    CommitMode.SYNC -> " · " + stringResource(R.string.advanced_mode_sync)
    CommitMode.REBUILD -> " · " + stringResource(R.string.advanced_mode_rebuild)
}