package com.vztechcore.vzsync.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnBackground

@Composable
fun VzPrimaryButton(
    text: String,
    isFolderSelected: Boolean,
    enabled: Boolean,
    isWorking: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "buttonScale",
    )
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 10.dp,
        animationSpec = tween(durationMillis = 120),
        label = "buttonElevation",
    )

    val icon: ImageVector =
        if (isFolderSelected) Icons.Rounded.CloudUpload else Icons.Rounded.FolderOpen

    val gradient = if (isFolderSelected) {
        Brush.horizontalGradient(listOf(DarkPrimary, DarkSecondary))
    } else {
        Brush.horizontalGradient(listOf(DarkPrimary, DarkPrimary))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale)
            .shadow(elevation, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled && !isWorking,
                onClick = onClick,
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isWorking) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(12.dp))
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = VzOnBackground,
                )
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = VzOnBackground,
            )
        }
    }
}