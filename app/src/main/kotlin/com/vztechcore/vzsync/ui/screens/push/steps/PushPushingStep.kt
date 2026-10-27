package com.vztechcore.vzsync.ui.screens.push.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.LogEntry
import com.vztechcore.vzsync.domain.push.LogStyle
import com.vztechcore.vzsync.ui.components.LogBlock
import com.vztechcore.vzsync.ui.components.TerminalBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant

@Composable
internal fun PushPushingStep(
    logs: List<LogEntry>,
    logStyle: LogStyle,
    folderPath: String,
) {
    Text(
        text = stringResource(R.string.push_pushing_title),
        style = MaterialTheme.typography.titleLarge,
        color = VzOnSurface,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = stringResource(R.string.push_pushing_subtitle),
        style = MaterialTheme.typography.bodySmall,
        color = VzOnSurfaceVariant,
    )
    Spacer(Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TerminalBackground)
            .padding(12.dp)
            .heightIn(min = 180.dp, max = 400.dp),
    ) {
        LogBlock(
            logs = logs,
            logStyle = logStyle,
            folderPath = folderPath,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}