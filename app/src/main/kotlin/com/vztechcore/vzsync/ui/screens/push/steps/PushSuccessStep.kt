package com.vztechcore.vzsync.ui.screens.push.steps

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Terminal
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.LogEntry
import com.vztechcore.vzsync.domain.push.LogStyle
import com.vztechcore.vzsync.domain.push.PostPushAction
import com.vztechcore.vzsync.ui.components.LogBlock
import com.vztechcore.vzsync.ui.components.TerminalBackground
import com.vztechcore.vzsync.ui.screens.push.PrimaryActionButton
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@Composable
internal fun PushSuccessStep(
    url: String,
    repoName: String,
    logs: List<LogEntry>,
    logStyle: LogStyle,
    folderPath: String,
    autoShowLog: Boolean,
    postPushAction: PostPushAction,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var showLogs by remember { mutableStateOf(autoShowLog) }

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
            text = stringResource(R.string.push_success_title),
            style = MaterialTheme.typography.headlineMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = successMessage(postPushAction, repoName),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
            textAlign = TextAlign.Center,
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
                text = stringResource(R.string.push_success_url),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurface,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                IconButton(onClick = { clipboard.setText(AnnotatedString(url)) }) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = stringResource(R.string.push_success_copy_url),
                        tint = VzOnSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
                IconButton(onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (_: Exception) {
                    }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.OpenInNew,
                        contentDescription = stringResource(R.string.push_success_open_url),
                        tint = DarkSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showLogs,
            enter = expandVertically(tween(200)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(180)) + fadeOut(tween(180)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
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
        PrimaryActionButton(
            text = stringResource(R.string.common_close),
            enabled = true,
            onClick = onClose,
        )
    }
}

@Composable
private fun successMessage(action: PostPushAction, repoName: String): String = when (action) {
    PostPushAction.COMMIT_ONLY ->
        stringResource(R.string.push_success_message_commit_only, repoName)
    PostPushAction.COMMIT_AND_PUSH ->
        stringResource(R.string.push_success_message_full, repoName)
}