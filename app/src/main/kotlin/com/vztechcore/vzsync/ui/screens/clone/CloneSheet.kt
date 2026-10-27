package com.vztechcore.vzsync.ui.screens.clone

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.data.git.CloneFailureReason
import com.vztechcore.vzsync.domain.LogEntry
import com.vztechcore.vzsync.domain.push.LogStyle
import com.vztechcore.vzsync.ui.components.LogBlock
import com.vztechcore.vzsync.ui.components.TerminalBackground
import com.vztechcore.vzsync.ui.theme.DarkError
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurface
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneSheet(
    state: CloneUiState,
    onDismiss: () -> Unit,
    onUrlChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onHide: () -> Unit,
    onCancel: () -> Unit,
    onOpenStorageSettings: () -> Unit,
    onHelpRequested: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true },
    )

    val isCloning = state.step == CloneStep.CLONING
    val properties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = !isCloning,
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (!isCloning) onDismiss()
        },
        sheetState = sheetState,
        properties = properties,
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
                CloneStep.INPUT -> CloneInputContent(
                    state = state,
                    onUrlChange = onUrlChange,
                    onConfirm = onConfirm,
                    onHelpRequested = onHelpRequested,
                )
                CloneStep.CLONING -> CloneCloningContent(
                    state = state,
                    onHide = onHide,
                    onCancel = onCancel,
                )
                CloneStep.SUCCESS -> CloneSuccessContent(
                    path = state.resultPath.orEmpty(),
                    repoName = state.resultRepoName.orEmpty(),
                    onClose = onDismiss,
                )
                CloneStep.ERROR -> CloneErrorContent(
                    errorMessage = state.errorMessage.orEmpty(),
                    errorReason = state.errorReason,
                    onRetry = onRetry,
                    onOpenStorageSettings = onOpenStorageSettings,
                    onClose = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun CloneInputContent(
    state: CloneUiState,
    onUrlChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onHelpRequested: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.clone_title),
                style = MaterialTheme.typography.titleLarge,
                color = VzOnSurface,
                fontWeight = FontWeight.Bold,
            )
        }
        IconButton(onClick = onHelpRequested) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                contentDescription = stringResource(R.string.clone_help_action),
                tint = VzOnSurfaceVariant,
            )
        }
    }
    Spacer(Modifier.height(6.dp))
    Text(
        text = stringResource(R.string.clone_subtitle),
        style = MaterialTheme.typography.bodySmall,
        color = VzOnSurfaceVariant,
    )
    Spacer(Modifier.height(16.dp))

    OutlinedTextField(
        value = state.url,
        onValueChange = onUrlChange,
        label = { Text(stringResource(R.string.clone_url_label)) },
        placeholder = { Text(stringResource(R.string.clone_url_placeholder)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DarkPrimary,
            unfocusedBorderColor = VzSurfaceVariant,
            focusedLabelColor = VzOnSurfaceVariant,
            unfocusedLabelColor = VzOnSurfaceVariant,
            focusedPlaceholderColor = VzOnSurfaceVariant.copy(alpha = 0.5f),
            unfocusedPlaceholderColor = VzOnSurfaceVariant.copy(alpha = 0.5f),
            cursorColor = DarkPrimary,
            focusedTextColor = VzOnSurface,
            unfocusedTextColor = VzOnSurface,
        ),
    )

    Spacer(Modifier.height(10.dp))

    Text(
        text = stringResource(R.string.clone_url_hint),
        style = MaterialTheme.typography.bodySmall,
        color = VzOnSurfaceVariant,
    )

    Spacer(Modifier.height(24.dp))

    Button(
        onClick = onConfirm,
        enabled = state.isUrlValid,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkPrimary,
            contentColor = VzOnSurface,
            disabledContainerColor = VzSurfaceVariant,
            disabledContentColor = VzOnSurfaceVariant,
        ),
    ) {
        Icon(
            imageVector = Icons.Rounded.Download,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.clone_button),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CloneCloningContent(
    state: CloneUiState,
    onHide: () -> Unit,
    onCancel: () -> Unit,
) {
    var showLogs by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.clone_cloning_title),
                style = MaterialTheme.typography.titleLarge,
                color = VzOnSurface,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = state.phase.ifBlank {
                    stringResource(R.string.clone_cloning_preparing)
                },
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }
        IconButton(onClick = { showLogs = !showLogs }) {
            Icon(
                imageVector = Icons.Rounded.Terminal,
                contentDescription = stringResource(
                    if (showLogs) R.string.common_hide_log else R.string.common_show_log
                ),
                tint = if (showLogs) DarkSecondary else VzOnSurfaceVariant,
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    if (state.isIndeterminate) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = DarkSecondary,
            trackColor = VzSurfaceVariant,
        )
    } else {
        LinearProgressIndicator(
            progress = { state.percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = DarkSecondary,
            trackColor = VzSurfaceVariant,
        )
    }

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = if (state.isIndeterminate) {
                stringResource(R.string.clone_cloning_calculating)
            } else {
                stringResource(R.string.clone_cloning_percentage, state.percentage)
            },
            style = MaterialTheme.typography.titleMedium,
            color = DarkSecondary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.clone_cloning_branding),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }

    AnimatedVisibility(
        visible = showLogs && state.logs.isNotEmpty(),
        enter = expandVertically(tween(200)) + fadeIn(tween(200)),
        exit = shrinkVertically(tween(180)) + fadeOut(tween(180)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TerminalBackground)
                .padding(12.dp)
                .heightIn(min = 150.dp, max = 300.dp),
        ) {
            LogBlock(
                logs = state.logs,
                logStyle = LogStyle.TERMINAL,
                folderPath = "",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    Spacer(Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Button(
            onClick = onHide,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VzSurfaceVariant,
                contentColor = VzOnSurface,
            ),
        ) {
            Icon(
                imageVector = Icons.Rounded.VisibilityOff,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.clone_hide),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Button(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkError.copy(alpha = 0.15f),
                contentColor = DarkError,
            ),
        ) {
            Icon(
                imageVector = Icons.Rounded.Cancel,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.common_cancel),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }

    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.clone_hide_hint),
        style = MaterialTheme.typography.bodySmall,
        color = VzOnSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun CloneSuccessContent(
    path: String,
    repoName: String,
    onClose: () -> Unit,
) {
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(DarkPrimary.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4ADE80),
                modifier = Modifier.size(56.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.clone_success_title),
            style = MaterialTheme.typography.headlineMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = repoName,
            style = MaterialTheme.typography.bodySmall,
            color = DarkSecondary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VzSurfaceVariant)
                .padding(start = 14.dp, end = 6.dp, top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.clone_success_path_label),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = path,
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurface,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                )
                IconButton(onClick = {
                    clipboard.setText(AnnotatedString(path))
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = stringResource(R.string.clone_success_copy_path),
                        tint = VzOnSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.clone_success_hint),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onClose,
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
                text = stringResource(R.string.clone_success_close),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun CloneErrorContent(
    errorMessage: String,
    errorReason: CloneFailureReason?,
    onRetry: () -> Unit,
    onOpenStorageSettings: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(DarkError.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Cancel,
                contentDescription = null,
                tint = DarkError,
                modifier = Modifier.size(48.dp),
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.clone_error_title),
            style = MaterialTheme.typography.headlineMedium,
            color = DarkError,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        if (errorReason == CloneFailureReason.STORAGE_PERMISSION_MISSING) {
            Button(
                onClick = onOpenStorageSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkError,
                    contentColor = VzOnSurface,
                ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.clone_error_open_settings),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = onRetry,
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
                text = stringResource(R.string.common_retry),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(10.dp))
        TextButton(
            onClick = onClose,
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