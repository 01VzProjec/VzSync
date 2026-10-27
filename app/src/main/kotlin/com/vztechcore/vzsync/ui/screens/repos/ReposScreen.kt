package com.vztechcore.vzsync.ui.screens.repos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.Repo
import com.vztechcore.vzsync.domain.help.HelpSection
import com.vztechcore.vzsync.domain.help.HelpTarget
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
fun ReposScreen(
    state: ReposUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onFilterChange: (RepoFilter) -> Unit,
    onRepoClick: (Repo) -> Unit,
    onDismissRepoSheet: () -> Unit,
    onRequestDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onFeedbackConsumed: () -> Unit,
    onHelpRequested: (HelpTarget) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.feedbackMessage) {
        val msg = state.feedbackMessage ?: return@LaunchedEffect
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
            ReposTopBar(
                onBack = onBack,
                onRefresh = onRefresh,
                onHelp = { onHelpRequested(HelpTarget(HelpSection.REPOS_SCREEN)) },
                isLoading = state.isLoading,
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { UserStatsCard(state = state) }

                item {
                    FilterChipsRow(
                        filter = state.filter,
                        onFilterChange = onFilterChange,
                        state = state,
                    )
                }

                when {
                    state.isLoading -> item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = DarkSecondary)
                        }
                    }

                    state.error != null -> item {
                        ErrorCard(message = state.error)
                    }

                    state.filtered.isEmpty() -> item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.repos_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = VzOnSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    else -> items(state.filtered, key = { it.fullName }) { repo ->
                        RepoCard(
                            repo = repo,
                            onClick = { onRepoClick(repo) },
                        )
                    }
                }
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

    state.selectedRepo?.let { repo ->
        RepoDetailSheet(
            repo = repo,
            fileCount = state.fileCounts[repo.fullName],
            fileCountError = state.fileCountErrors[repo.fullName],
            isDeleting = state.isDeleting,
            deleteError = state.deleteError,
            onDismiss = onDismissRepoSheet,
            onDeleteClick = onRequestDelete,
        )
    }

    if (state.showDeleteConfirm && state.selectedRepo != null) {
        DeleteConfirmDialog(
            repo = state.selectedRepo,
            isDeleting = state.isDeleting,
            deleteError = state.deleteError,
            onConfirm = onConfirmDelete,
            onDismiss = onCancelDelete,
        )
    }
}

@Composable
private fun ReposTopBar(
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onHelp: () -> Unit,
    isLoading: Boolean,
) {
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
            text = stringResource(R.string.repos_title),
            style = MaterialTheme.typography.titleLarge,
            color = VzOnSurface,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onHelp) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                contentDescription = stringResource(R.string.repos_help_action),
                tint = VzOnSurface,
            )
        }
        IconButton(onClick = onRefresh, enabled = !isLoading) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = stringResource(R.string.repos_refresh),
                tint = if (isLoading) VzOnSurfaceVariant else VzOnSurface,
            )
        }
    }
}

@Composable
private fun UserStatsCard(state: ReposUiState) {
    val profile = state.profile ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(VzSurfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = profile.avatarUrl,
                contentDescription = stringResource(
                    R.string.topbar_profile_photo,
                    profile.displayName,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = VzOnSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "@${profile.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            MiniStat(stringResource(R.string.repos_stat_repos), state.totalRepos.toString())
            MiniStat(stringResource(R.string.repos_stat_public), state.publicCount.toString())
            MiniStat(stringResource(R.string.repos_stat_private), state.privateCount.toString())
            MiniStat(stringResource(R.string.repos_stat_stars), state.totalStars.toString())
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}

@Composable
private fun FilterChipsRow(
    filter: RepoFilter,
    onFilterChange: (RepoFilter) -> Unit,
    state: ReposUiState,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        FilterChip(
            label = stringResource(R.string.repos_filter_all, state.totalRepos),
            selected = filter == RepoFilter.All,
            onClick = { onFilterChange(RepoFilter.All) },
        )
        FilterChip(
            label = stringResource(R.string.repos_filter_public, state.publicCount),
            selected = filter == RepoFilter.Public,
            onClick = { onFilterChange(RepoFilter.Public) },
        )
        FilterChip(
            label = stringResource(R.string.repos_filter_private, state.privateCount),
            selected = filter == RepoFilter.Private,
            onClick = { onFilterChange(RepoFilter.Private) },
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) DarkPrimary else VzSurfaceVariant
    val border = if (selected) DarkPrimary else VzSurfaceVariant
    val fg = VzOnSurface

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = fg,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun RepoCard(repo: Repo, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VzSurfaceVariant)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (repo.isPrivate) Icons.Rounded.Lock else Icons.Rounded.Public,
                contentDescription = null,
                tint = if (repo.isPrivate) DarkSecondary else VzOnSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = repo.name,
                style = MaterialTheme.typography.titleMedium,
                color = VzOnSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (repo.isPrivate) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSecondary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = stringResource(R.string.repos_private_badge),
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        if (!repo.description.isNullOrBlank()) {
            Text(
                text = repo.description,
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (repo.language != null) {
                Text(
                    text = repo.language,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkSecondary,
                    fontWeight = FontWeight.Medium,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = VzOnSurfaceVariant,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = repo.stars.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                )
            }
            Text(
                text = stringResource(
                    R.string.repos_updated_at,
                    formatDate(repo.updatedAtIso),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkError.copy(alpha = 0.12f))
            .padding(16.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = DarkError,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepoDetailSheet(
    repo: Repo,
    fileCount: Int?,
    fileCountError: String?,
    isDeleting: Boolean,
    deleteError: String?,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

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
                .padding(bottom = 32.dp)
                .heightIn(max = 640.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (repo.isPrivate) Icons.Rounded.Lock else Icons.Rounded.Public,
                    contentDescription = null,
                    tint = DarkSecondary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = repo.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    color = VzOnSurface,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.weight(1f),
                )
            }

            if (!repo.description.isNullOrBlank()) {
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(VzSurfaceVariant)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                DetailRow(
                    label = stringResource(R.string.repos_detail_visibility),
                    value = stringResource(
                        if (repo.isPrivate) R.string.repos_detail_private
                        else R.string.repos_detail_public
                    ),
                )
                DetailRow(
                    label = stringResource(R.string.repos_detail_branch),
                    value = repo.defaultBranch,
                    mono = true,
                )
                if (repo.language != null) {
                    DetailRow(
                        label = stringResource(R.string.repos_detail_language),
                        value = repo.language,
                    )
                }
                DetailRow(
                    label = stringResource(R.string.repos_detail_stars),
                    value = repo.stars.toString(),
                )
                DetailRow(
                    label = stringResource(R.string.repos_detail_forks),
                    value = repo.forks.toString(),
                )
                DetailRow(
                    label = stringResource(R.string.repos_detail_watchers),
                    value = repo.watchers.toString(),
                )
                DetailRow(
                    label = stringResource(R.string.repos_detail_issues),
                    value = repo.openIssues.toString(),
                )
                DetailRow(
                    label = stringResource(R.string.repos_detail_size),
                    value = formatSize(repo.sizeKb),
                )
                DetailRow(
                    label = stringResource(R.string.repos_detail_files),
                    value = when {
                        fileCountError != null -> "—"
                        fileCount != null -> fileCount.toString()
                        else -> stringResource(R.string.repos_detail_counting)
                    },
                )
                DetailRow(
                    label = stringResource(R.string.repos_detail_updated),
                    value = formatDateLong(repo.updatedAtIso),
                )
            }

            if (fileCountError != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Amber.copy(alpha = 0.10f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = fileCountError,
                        style = MaterialTheme.typography.bodySmall,
                        color = Amber,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = {
                        try {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                            )
                        } catch (_: Exception) {
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkPrimary,
                        contentColor = VzOnSurface,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.repos_open_github),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (deleteError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkError.copy(alpha = 0.12f))
                        .padding(12.dp),
                ) {
                    Text(
                        text = deleteError,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkError,
                    )
                }
            }

            Button(
                onClick = onDeleteClick,
                enabled = !isDeleting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkError.copy(alpha = 0.15f),
                    contentColor = DarkError,
                    disabledContainerColor = VzSurfaceVariant,
                    disabledContentColor = VzOnSurfaceVariant,
                ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.repos_delete),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.common_close),
                    style = MaterialTheme.typography.bodyMedium,
                    color = VzOnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    repo: Repo,
    isDeleting: Boolean,
    deleteError: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        containerColor = VzSurface,
        titleContentColor = VzOnSurface,
        textContentColor = VzOnSurfaceVariant,
        title = {
            Text(
                text = stringResource(R.string.repos_delete_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.repos_delete_about),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = repo.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkError,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
                Text(
                    text = stringResource(R.string.repos_delete_warning),
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Amber.copy(alpha = 0.10f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = stringResource(R.string.repos_delete_scope_warning),
                        style = MaterialTheme.typography.bodySmall,
                        color = Amber,
                    )
                }
                if (deleteError != null) {
                    Text(
                        text = deleteError,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkError,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isDeleting,
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = DarkError,
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = stringResource(
                        if (isDeleting) R.string.repos_delete_in_progress
                        else R.string.repos_delete_confirm
                    ),
                    color = DarkError,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting,
            ) {
                Text(
                    text = stringResource(R.string.common_cancel),
                    color = VzOnSurfaceVariant,
                )
            }
        },
    )
}

@Composable
private fun DetailRow(label: String, value: String, mono: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
            modifier = Modifier.width(130.dp),
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

private fun formatSize(sizeKb: Int): String {
    if (sizeKb <= 0) return "0 KB"
    val mb = sizeKb / 1024.0
    return if (mb >= 1.0) {
        String.format(Locale.US, "%.2f MB", mb)
    } else {
        "$sizeKb KB"
    }
}

private fun formatDate(iso: String): String {
    return try {
        val instant = Instant.parse(iso)
        val formatter = DateTimeFormatter
            .ofPattern("dd/MM/yyyy")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (_: Exception) {
        iso.take(10)
    }
}

private fun formatDateLong(iso: String): String {
    return try {
        val instant = Instant.parse(iso)
        val formatter = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (_: Exception) {
        iso.take(10)
    }
}