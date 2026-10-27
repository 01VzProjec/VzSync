package com.vztechcore.vzsync.ui.screens.push.steps

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.data.git.FailureReason
import com.vztechcore.vzsync.domain.LogEntry
import com.vztechcore.vzsync.domain.push.LogStyle
import com.vztechcore.vzsync.ui.components.LogBlock
import com.vztechcore.vzsync.ui.components.TerminalBackground
import com.vztechcore.vzsync.ui.theme.DarkError
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@Composable
internal fun PushErrorStep(
    errorMessage: String,
    errorReason: FailureReason?,
    logs: List<LogEntry>,
    logStyle: LogStyle,
    folderPath: String,
    onRetry: () -> Unit,
    onOpenStorageSettings: () -> Unit,
    onClose: () -> Unit,
) {
    var showLogs by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(DarkError.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Cancel,
                contentDescription = null,
                tint = DarkError,
                modifier = Modifier.size(56.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.push_error_title),
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

        AnimatedVisibility(
            visible = showLogs && logs.isNotEmpty(),
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
                    .heightIn(max = 320.dp),
            ) {
                LogBlock(
                    logs = logs,
                    logStyle = logStyle,
                    folderPath = folderPath,
                    modifier = Modifier.fillMaxWidth(),
                    autoScroll = false,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        if (errorReason == FailureReason.STORAGE_PERMISSION_MISSING) {
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
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.push_error_open_settings),
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

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VzSurfaceVariant,
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