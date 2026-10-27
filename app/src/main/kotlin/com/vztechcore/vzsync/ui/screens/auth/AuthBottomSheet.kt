package com.vztechcore.vzsync.ui.screens.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.ui.theme.DarkError
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurface
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthBottomSheet(
    state: AuthUiState,
    onUsernameChange: (String) -> Unit,
    onTokenChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onSubmit: () -> Unit,
    onLogout: () -> Unit,
    onViewRepos: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoggedIn && state.currentProfile != null) {
                LoggedInContent(
                    state = state,
                    onLogout = onLogout,
                    onViewRepos = onViewRepos,
                )
            } else {
                LoginContent(
                    state = state,
                    onUsernameChange = onUsernameChange,
                    onTokenChange = onTokenChange,
                    onToggleVisibility = onToggleVisibility,
                    onSubmit = onSubmit,
                )
            }
        }
    }
}

@Composable
private fun LoginContent(
    state: AuthUiState,
    onUsernameChange: (String) -> Unit,
    onTokenChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onSubmit: () -> Unit,
) {
    Text(
        text = stringResource(R.string.auth_title),
        style = MaterialTheme.typography.titleLarge,
        color = VzOnSurface,
    )

    TokenExplanationCard()

    VzTextField(
        value = state.username,
        onValueChange = onUsernameChange,
        label = stringResource(R.string.auth_username_label),
        enabled = !state.isLoading,
    )

    VzTextField(
        value = state.token,
        onValueChange = onTokenChange,
        label = stringResource(R.string.auth_token_label),
        enabled = !state.isLoading,
        isPassword = !state.isTokenVisible,
        keyboardType = KeyboardType.Password,
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (state.isTokenVisible) {
                        Icons.Rounded.VisibilityOff
                    } else {
                        Icons.Rounded.Visibility
                    },
                    contentDescription = stringResource(
                        if (state.isTokenVisible) R.string.auth_token_hide
                        else R.string.auth_token_show
                    ),
                    tint = VzOnSurfaceVariant,
                )
            }
        },
    )

    if (state.errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkError.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = state.errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = DarkError,
            )
        }
    }

    Spacer(Modifier.height(4.dp))

    Button(
        onClick = onSubmit,
        enabled = !state.isLoading && state.username.isNotBlank() && state.token.isNotBlank(),
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
            text = stringResource(
                if (state.isLoading) R.string.auth_connecting else R.string.auth_connect
            ),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun TokenExplanationCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VzSurfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.auth_why_title),
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = stringResource(R.string.auth_why_body),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.auth_scopes_title),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
        TokenScopeItem(
            scope = stringResource(R.string.auth_scope_repo),
            description = stringResource(R.string.auth_scope_repo_desc),
        )
        TokenScopeItem(
            scope = stringResource(R.string.auth_scope_workflow),
            description = stringResource(R.string.auth_scope_workflow_desc),
        )
        TokenScopeItem(
            scope = stringResource(R.string.auth_scope_read_user),
            description = stringResource(R.string.auth_scope_read_user_desc),
        )
        Text(
            text = stringResource(R.string.auth_where_create),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}

@Composable
private fun TokenScopeItem(scope: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "✓",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkSecondary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = scope,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}

@Composable
private fun LoggedInContent(
    state: AuthUiState,
    onLogout: () -> Unit,
    onViewRepos: () -> Unit,
) {
    val profile = state.currentProfile ?: return

    Text(
        text = stringResource(R.string.auth_connected_title),
        style = MaterialTheme.typography.titleLarge,
        color = VzOnSurface,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VzSurfaceVariant)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
        ) {
            AsyncImage(
                model = profile.avatarUrl,
                contentDescription = stringResource(
                    R.string.topbar_profile_photo,
                    profile.displayName,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = profile.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = VzOnSurface,
            )
            Text(
                text = "@${profile.username}",
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
            if (!profile.bio.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                    maxLines = 2,
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VzSurfaceVariant)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatItem(
            label = stringResource(R.string.auth_stat_repos),
            value = profile.publicRepos.toString(),
        )
        StatItem(
            label = stringResource(R.string.auth_stat_followers),
            value = profile.followers.toString(),
        )
        StatItem(
            label = stringResource(R.string.auth_stat_following),
            value = profile.following.toString(),
        )
    }

    Button(
        onClick = onViewRepos,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkPrimary,
            contentColor = VzOnSurface,
        ),
    ) {
        Icon(
            imageVector = Icons.Rounded.Folder,
            contentDescription = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.auth_view_repos),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }

    Button(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = VzSurfaceVariant,
            contentColor = VzOnSurface,
        ),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Logout,
            contentDescription = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.auth_disconnect),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = VzOnSurface,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}

@Composable
private fun VzTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DarkPrimary,
            unfocusedBorderColor = VzSurfaceVariant,
            focusedLabelColor = VzOnSurfaceVariant,
            unfocusedLabelColor = VzOnSurfaceVariant,
            cursorColor = DarkPrimary,
            focusedTextColor = VzOnSurface,
            unfocusedTextColor = VzOnSurface,
        ),
    )
}