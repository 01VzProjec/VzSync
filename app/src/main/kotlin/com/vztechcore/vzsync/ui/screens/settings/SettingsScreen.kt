package com.vztechcore.vzsync.ui.screens.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.ThemeMode
import com.vztechcore.vzsync.domain.help.HelpSection
import com.vztechcore.vzsync.domain.help.HelpTarget
import com.vztechcore.vzsync.domain.language.AppLanguage
import com.vztechcore.vzsync.domain.push.PushSettings
import com.vztechcore.vzsync.ui.components.VzFooter
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurface

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onAboutClick: () -> Unit,
    onLicensesClick: () -> Unit,
    onPushSettingsClick: () -> Unit,
    onAdvancedActionsClick: () -> Unit,
    onHelpRequested: (HelpTarget) -> Unit,
) {
    var showThemeSheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VzBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            SettingsTopBar(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(Modifier.height(8.dp))

                SectionHeader(stringResource(R.string.settings_section_appearance))
                SettingsRow(
                    icon = Icons.Rounded.Palette,
                    title = stringResource(R.string.settings_theme),
                    subtitle = stringResource(R.string.settings_theme_subtitle),
                    trailingText = state.themeMode.label,
                    onClick = { showThemeSheet = true },
                )
                SettingsRow(
                    icon = Icons.Rounded.Language,
                    title = stringResource(R.string.settings_language),
                    subtitle = stringResource(R.string.settings_language_subtitle),
                    trailingText = stringResource(state.language.displayNameRes),
                    onClick = { showLanguageSheet = true },
                )

                Spacer(Modifier.height(20.dp))

                SectionHeader(stringResource(R.string.settings_section_system))
                SettingsRow(
                    icon = Icons.Rounded.CloudUpload,
                    title = stringResource(R.string.settings_push),
                    subtitle = pushSubtitle(state.pushSettings),
                    onClick = onPushSettingsClick,
                )
                SettingsRow(
                    icon = Icons.Rounded.Tune,
                    title = stringResource(R.string.settings_advanced_actions),
                    subtitle = stringResource(R.string.settings_advanced_actions_subtitle),
                    onClick = onAdvancedActionsClick,
                )

                Spacer(Modifier.height(20.dp))

                SectionHeader(stringResource(R.string.settings_section_info))
                SettingsRow(
                    icon = Icons.Rounded.Info,
                    title = stringResource(R.string.settings_about),
                    subtitle = stringResource(R.string.settings_about_subtitle),
                    onClick = onAboutClick,
                )
                SettingsRow(
                    icon = Icons.AutoMirrored.Rounded.HelpOutline,
                    title = stringResource(R.string.settings_help),
                    subtitle = stringResource(R.string.settings_help_subtitle),
                    onClick = { onHelpRequested(HelpTarget(HelpSection.GETTING_STARTED)) },
                )
                SettingsRow(
                    icon = Icons.Rounded.LibraryBooks,
                    title = stringResource(R.string.settings_libraries),
                    subtitle = stringResource(R.string.settings_libraries_subtitle),
                    onClick = onLicensesClick,
                )

                Spacer(Modifier.height(24.dp))
                VzFooter()
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    if (showThemeSheet) {
        ThemePickerSheet(
            currentMode = state.themeMode,
            onSelect = { mode ->
                onThemeChange(mode)
                showThemeSheet = false
            },
            onHelp = {
                showThemeSheet = false
                onHelpRequested(HelpTarget(HelpSection.THEME))
            },
            onDismiss = { showThemeSheet = false },
        )
    }

    if (showLanguageSheet) {
        LanguagePickerSheet(
            currentLanguage = state.language,
            onSelect = { language ->
                onLanguageChange(language)
                showLanguageSheet = false
            },
            onDismiss = { showLanguageSheet = false },
        )
    }
}

private fun pushSubtitle(settings: PushSettings): String {
    return "${settings.defaultMode.label} · ${settings.postPushAction.shortLabel}"
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.common_back),
                tint = VzOnSurface,
            )
        }
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.bodySmall,
        color = VzOnSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingText: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) VzOnSurface else VzOnSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (enabled) VzOnSurface else VzOnSurfaceVariant.copy(alpha = 0.5f),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant.copy(alpha = if (enabled) 1f else 0.4f),
            )
        }
        if (trailingText != null) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }
        if (enabled) {
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = VzOnSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemePickerSheet(
    currentMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onHelp: () -> Unit,
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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_theme_sheet_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = VzOnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.settings_theme_sheet_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = VzOnSurfaceVariant,
                    )
                }
                IconButton(onClick = onHelp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                        contentDescription = stringResource(R.string.settings_help_action),
                        tint = VzOnSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            ThemeMode.entries.forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelect(mode) }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = mode == currentMode,
                        onClick = { onSelect(mode) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = DarkSecondary,
                            unselectedColor = VzOnSurfaceVariant,
                        ),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = mode.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = VzOnSurface,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguagePickerSheet(
    currentLanguage: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
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
        ) {
            Text(
                text = stringResource(R.string.settings_language_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                color = VzOnSurface,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.settings_language_sheet_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            AppLanguage.entries.forEach { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelect(language) }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = language == currentLanguage,
                        onClick = { onSelect(language) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = DarkSecondary,
                            unselectedColor = VzOnSurfaceVariant,
                        ),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(language.displayNameRes),
                        style = MaterialTheme.typography.titleMedium,
                        color = VzOnSurface,
                    )
                }
            }
        }
    }
}