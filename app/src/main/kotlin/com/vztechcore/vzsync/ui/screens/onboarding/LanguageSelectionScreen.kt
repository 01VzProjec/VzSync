package com.vztechcore.vzsync.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.language.AppLanguage
import com.vztechcore.vzsync.ui.components.VzPrimaryButton
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@Composable
fun LanguageSelectionScreen(
    onConfirm: (AppLanguage) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val systemLanguage = remember(configuration) {
        val tag = configuration.locales[0]?.toLanguageTag() ?: "pt-BR"
        AppLanguage.fromTag(tag)
    }
    var selected by remember { mutableStateOf(systemLanguage) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VzBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(DarkPrimary.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Language,
                contentDescription = null,
                tint = DarkSecondary,
                modifier = Modifier.size(40.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.language_select_title),
            style = MaterialTheme.typography.headlineMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.language_select_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = VzOnSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(32.dp))

        AppLanguage.entries.forEach { language ->
            LanguageOption(
                language = language,
                selected = language == selected,
                onClick = { selected = language },
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(24.dp))

        VzPrimaryButton(
            text = stringResource(R.string.language_confirm),
            isFolderSelected = true,
            enabled = true,
            onClick = { onConfirm(selected) },
        )
    }
}

@Composable
private fun LanguageOption(
    language: AppLanguage,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) DarkSecondary else VzSurfaceVariant
    val bgColor = if (selected) DarkPrimary.copy(alpha = 0.15f) else VzSurfaceVariant.copy(alpha = 0.4f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(language.displayNameRes),
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = DarkSecondary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}