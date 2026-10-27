package com.vztechcore.vzsync.ui.screens.push.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.Repo
import com.vztechcore.vzsync.domain.push.PushMode
import com.vztechcore.vzsync.ui.screens.push.Amber
import com.vztechcore.vzsync.ui.screens.push.PrimaryActionButton
import com.vztechcore.vzsync.ui.screens.push.StepHeader
import com.vztechcore.vzsync.ui.screens.push.WarningBanner
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@Composable
internal fun PushConfirmRepoStep(
    repo: Repo?,
    pushMode: PushMode,
    onBack: () -> Unit,
    onModeChange: (PushMode) -> Unit,
    onConfirm: () -> Unit,
) {
    var showModeMenu by remember { mutableStateOf(false) }

    StepHeader(
        title = stringResource(R.string.push_confirm_title),
        onBack = onBack,
        trailing = {
            Box {
                IconButton(onClick = { showModeMenu = true }) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.push_confirm_mode_action),
                        tint = VzOnSurface,
                    )
                }
                DropdownMenu(
                    expanded = showModeMenu,
                    onDismissRequest = { showModeMenu = false },
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = stringResource(R.string.push_confirm_sync),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = stringResource(R.string.push_confirm_sync_subtitle),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Sync,
                                contentDescription = null,
                            )
                        },
                        trailingIcon = if (pushMode == PushMode.SYNC) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = DarkSecondary,
                                )
                            }
                        } else null,
                        onClick = {
                            onModeChange(PushMode.SYNC)
                            showModeMenu = false
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = stringResource(R.string.push_confirm_rebuild),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = stringResource(R.string.push_confirm_rebuild_subtitle),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.RestartAlt,
                                contentDescription = null,
                            )
                        },
                        trailingIcon = if (pushMode == PushMode.REBUILD) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = DarkSecondary,
                                )
                            }
                        } else null,
                        onClick = {
                            onModeChange(PushMode.REBUILD)
                            showModeMenu = false
                        },
                    )
                }
            }
        },
    )

    if (repo == null) {
        Text(
            text = stringResource(R.string.push_confirm_empty),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
        return
    }

    Spacer(Modifier.height(12.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VzSurfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (repo.isPrivate) Icons.Rounded.Lock else Icons.Rounded.Public,
                contentDescription = null,
                tint = DarkSecondary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = repo.fullName,
                style = MaterialTheme.typography.titleMedium,
                color = VzOnSurface,
                fontWeight = FontWeight.SemiBold,
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
    }

    Spacer(Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.push_confirm_how),
        style = MaterialTheme.typography.titleMedium,
        color = VzOnSurface,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VzSurfaceVariant)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = if (pushMode == PushMode.SYNC) Icons.Rounded.Sync
            else Icons.Rounded.RestartAlt,
            contentDescription = null,
            tint = if (pushMode == PushMode.SYNC) DarkSecondary else Amber,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (pushMode == PushMode.SYNC) {
                    stringResource(R.string.push_confirm_sync)
                } else {
                    stringResource(R.string.push_confirm_rebuild)
                },
                style = MaterialTheme.typography.titleMedium,
                color = VzOnSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (pushMode == PushMode.SYNC) {
                    stringResource(R.string.push_confirm_sync_subtitle)
                } else {
                    stringResource(R.string.push_confirm_rebuild_subtitle)
                },
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }
    }

    if (pushMode == PushMode.REBUILD) {
        Spacer(Modifier.height(12.dp))
        WarningBanner(text = stringResource(R.string.push_confirm_rebuild_warning))
    }

    Spacer(Modifier.height(24.dp))
    PrimaryActionButton(
        text = if (pushMode == PushMode.REBUILD) {
            stringResource(R.string.push_confirm_rebuild_button)
        } else {
            stringResource(R.string.push_confirm_button)
        },
        enabled = true,
        onClick = onConfirm,
    )
}