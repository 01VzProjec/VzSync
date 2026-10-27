package com.vztechcore.vzsync.ui.screens.push.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.ui.screens.push.ActionCard
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant

@Composable
internal fun PushSelectActionStep(
    onCreate: () -> Unit,
    onUpdate: () -> Unit,
    onHelpRequested: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.push_select_action_title),
                style = MaterialTheme.typography.titleLarge,
                color = VzOnSurface,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.push_select_action_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }
        IconButton(onClick = onHelpRequested) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                contentDescription = stringResource(R.string.push_action_help),
                tint = VzOnSurfaceVariant,
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    ActionCard(
        title = stringResource(R.string.push_action_create_title),
        subtitle = stringResource(R.string.push_action_create_subtitle),
        icon = Icons.Rounded.Add,
        onClick = onCreate,
    )
    Spacer(Modifier.height(10.dp))
    ActionCard(
        title = stringResource(R.string.push_action_existing_title),
        subtitle = stringResource(R.string.push_action_existing_subtitle),
        icon = Icons.Rounded.Public,
        onClick = onUpdate,
    )
}