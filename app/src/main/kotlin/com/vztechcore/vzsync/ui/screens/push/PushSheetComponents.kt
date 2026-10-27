package com.vztechcore.vzsync.ui.screens.push

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.domain.Repo
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

internal val Amber = Color(0xFFF59E0B)

@Composable
internal fun StepHeader(
    title: String,
    onBack: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = VzOnSurface,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = VzOnSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke()
    }
}

@Composable
internal fun PrimaryActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
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
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
internal fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VzSurfaceVariant)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(DarkPrimary.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DarkSecondary,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = VzOnSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun VisibilityOption(
    label: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = DarkSecondary,
                unselectedColor = VzOnSurfaceVariant,
            ),
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = VzOnSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun WarningBanner(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Amber.copy(alpha = 0.12f))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = null,
            tint = Amber,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Amber,
        )
    }
}

@Composable
internal fun RepoPickCard(repo: Repo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VzSurfaceVariant)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (repo.isPrivate) Icons.Rounded.Lock else Icons.Rounded.Public,
            contentDescription = null,
            tint = if (repo.isPrivate) DarkSecondary else VzOnSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = repo.name,
                style = MaterialTheme.typography.titleMedium,
                color = VzOnSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!repo.description.isNullOrBlank()) {
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
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
    }
}