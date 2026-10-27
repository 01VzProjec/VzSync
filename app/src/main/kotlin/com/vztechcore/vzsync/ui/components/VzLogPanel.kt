package com.vztechcore.vzsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.domain.LogEntry
import com.vztechcore.vzsync.domain.LogLevel
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurface
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VzLogPanel(
    logs: List<LogEntry>,
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
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(
                text = "Log do push",
                style = MaterialTheme.typography.titleLarge,
                color = VzOnSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${logs.count { it.level == LogLevel.ERROR }} erro(s) | " +
                    "${logs.count { it.level == LogLevel.SUCCESS }} sucesso(s)",
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VzSurfaceVariant)
                    .padding(vertical = 12.dp),
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(logs) { entry ->
                        LogLineItem(entry = entry)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}