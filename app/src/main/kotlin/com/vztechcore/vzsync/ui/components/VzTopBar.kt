package com.vztechcore.vzsync.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.UserProfile
import com.vztechcore.vzsync.ui.theme.VzOnBackground
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@Composable
fun VzTopBar(
    profile: UserProfile?,
    onSettingsClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            color = VzOnBackground,
            modifier = Modifier.weight(1f),
        )

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = stringResource(R.string.topbar_settings_action),
                tint = VzOnBackground,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(VzSurfaceVariant)
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = profile,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "avatar",
            ) { current ->
                if (current != null) {
                    AsyncImage(
                        model = current.avatarUrl,
                        contentDescription = stringResource(
                            R.string.topbar_profile_photo,
                            current.displayName,
                        ),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = stringResource(R.string.topbar_sign_in),
                        tint = VzOnBackground,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}