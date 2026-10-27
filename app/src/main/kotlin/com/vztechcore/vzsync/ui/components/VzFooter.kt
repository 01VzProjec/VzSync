package com.vztechcore.vzsync.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant

@Composable
fun VzFooter(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.footer_personal_project) +
                " | " +
                stringResource(R.string.footer_branding),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant.copy(alpha = 0.55f),
        )
    }
}