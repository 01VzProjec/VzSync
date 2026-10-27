package com.vztechcore.vzsync.ui.screens.push.steps

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.ui.screens.push.PrimaryActionButton
import com.vztechcore.vzsync.ui.screens.push.PushUiState
import com.vztechcore.vzsync.ui.screens.push.StepHeader
import com.vztechcore.vzsync.ui.screens.push.VisibilityOption
import com.vztechcore.vzsync.ui.screens.push.WarningBanner
import com.vztechcore.vzsync.ui.theme.DarkError
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@Composable
internal fun PushCreateRepoStep(
    state: PushUiState,
    onNameChange: (String) -> Unit,
    onVisibilityChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    StepHeader(
        title = stringResource(R.string.push_create_title),
        onBack = onBack,
    )
    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = state.repoNameInput,
        onValueChange = onNameChange,
        label = { Text(stringResource(R.string.push_create_name_label)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
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

    Spacer(Modifier.height(8.dp))

    if (state.repoNameInput.isNotBlank()) {
        if (state.isSanitizedNameValid) {
            Text(
                text = stringResource(R.string.push_create_sanitized, state.sanitizedName),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                fontFamily = FontFamily.Monospace,
            )
        } else {
            Text(
                text = stringResource(R.string.push_create_name_invalid),
                style = MaterialTheme.typography.bodySmall,
                color = DarkError,
            )
        }
    }

    Spacer(Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.push_create_visibility),
        style = MaterialTheme.typography.titleMedium,
        color = VzOnSurface,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(8.dp))

    VisibilityOption(
        label = stringResource(R.string.push_create_public),
        subtitle = stringResource(R.string.push_create_public_subtitle),
        selected = !state.isPrivate,
        onClick = { onVisibilityChange(false) },
    )
    Spacer(Modifier.height(6.dp))
    VisibilityOption(
        label = stringResource(R.string.push_create_private),
        subtitle = stringResource(R.string.push_create_private_subtitle),
        selected = state.isPrivate,
        onClick = { onVisibilityChange(true) },
    )

    if (state.isPrivate) {
        Spacer(Modifier.height(12.dp))
        WarningBanner(text = stringResource(R.string.push_create_private_warning))
    }

    Spacer(Modifier.height(24.dp))
    PrimaryActionButton(
        text = stringResource(R.string.push_create_confirm),
        enabled = state.isSanitizedNameValid,
        onClick = onConfirm,
    )
}