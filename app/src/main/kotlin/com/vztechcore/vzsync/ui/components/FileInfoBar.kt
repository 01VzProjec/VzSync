package com.vztechcore.vzsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@Composable
fun FileInfoBar(
    fileName: String,
    onMore: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(16.dp))
            .background(VzSurfaceVariant)
            .padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = null,
            tint = DarkSecondary,
            modifier = Modifier.size(18.dp),
        )
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Não é possível visualizar arquivos",
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.padding(horizontal = 2.dp))
            Text(
                text = "· $fileName",
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        TextButton(onClick = onMore) {
            Text(
                text = "Mais",
                style = MaterialTheme.typography.bodySmall,
                color = DarkSecondary,
                fontWeight = FontWeight.SemiBold,
            )
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Fechar notificação",
                tint = VzOnSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}