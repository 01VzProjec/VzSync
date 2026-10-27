package com.vztechcore.vzsync.ui.screens.settings.push

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.push.DefaultPushMode
import com.vztechcore.vzsync.domain.push.LogStyle
import com.vztechcore.vzsync.domain.push.PostPushAction
import com.vztechcore.vzsync.domain.push.PushSettings
import com.vztechcore.vzsync.ui.components.VzFooter
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private data class PlaceholderItem(
    val code: String,
    val descriptionRes: Int,
)

private val PLACEHOLDERS = listOf(
    PlaceholderItem("{date}", R.string.push_settings_placeholder_date),
    PlaceholderItem("{time}", R.string.push_settings_placeholder_time),
    PlaceholderItem("{folder}", R.string.push_settings_placeholder_folder),
    PlaceholderItem("{repo}", R.string.push_settings_placeholder_repo),
    PlaceholderItem("{user}", R.string.push_settings_placeholder_user),
    PlaceholderItem("{branch}", R.string.push_settings_placeholder_branch),
    PlaceholderItem("{files}", R.string.push_settings_placeholder_files),
    PlaceholderItem("{mode}", R.string.push_settings_placeholder_mode),
)

@Composable
fun PushSettingsScreen(
    settings: PushSettings,
    onBack: () -> Unit,
    onChange: (PushSettings) -> Unit,
    onHelpRequested: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VzBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        TopBar(onBack = onBack, onHelp = onHelpRequested)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            SectionHeader(stringResource(R.string.push_settings_section_behavior))
            DropdownRow(
                icon = Icons.Rounded.CloudUpload,
                title = stringResource(R.string.push_settings_default_mode),
                subtitle = stringResource(R.string.push_settings_default_mode_subtitle),
                currentValue = defaultModeLabel(settings.defaultMode),
                options = DefaultPushMode.entries.toList(),
                optionLabel = { defaultModeLabel(it) },
                onSelect = { onChange(settings.copy(defaultMode = it)) },
            )

            Spacer(Modifier.height(16.dp))
            SectionHeader(stringResource(R.string.push_settings_post_send))
            PostPushAction.entries.forEach { action ->
                RadioRow(
                    title = postActionLabel(action),
                    subtitle = subtitleForAction(action),
                    selected = settings.postPushAction == action,
                    onClick = { onChange(settings.copy(postPushAction = action)) },
                )
            }

            Spacer(Modifier.height(16.dp))
            SectionHeader(stringResource(R.string.push_settings_commit_message))
            OutlinedTextField(
                value = settings.commitMessageTemplate,
                onValueChange = { onChange(settings.copy(commitMessageTemplate = it)) },
                label = { Text(stringResource(R.string.push_settings_commit_message_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                trailingIcon = {
                    PlaceholderMenu(
                        onSelect = { code ->
                            onChange(
                                settings.copy(
                                    commitMessageTemplate =
                                        settings.commitMessageTemplate + " $code",
                                )
                            )
                        },
                    )
                },
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
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.push_settings_preview),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = resolvePreview(settings.commitMessageTemplate),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurface,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
            )

            Spacer(Modifier.height(16.dp))
            SectionHeader(stringResource(R.string.push_settings_section_log))
            DropdownRow(
                icon = Icons.Rounded.Terminal,
                title = stringResource(R.string.push_settings_log_style),
                subtitle = stringResource(R.string.push_settings_log_style_subtitle),
                currentValue = logStyleLabel(settings.logStyle),
                options = LogStyle.entries.toList(),
                optionLabel = { logStyleLabel(it) },
                onSelect = { onChange(settings.copy(logStyle = it)) },
            )
            Spacer(Modifier.height(8.dp))
            SwitchRow(
                icon = Icons.AutoMirrored.Rounded.Article,
                title = stringResource(R.string.push_settings_auto_log),
                subtitle = stringResource(R.string.push_settings_auto_log_subtitle),
                checked = settings.autoShowLog,
                onCheckedChange = { onChange(settings.copy(autoShowLog = it)) },
            )

            Spacer(Modifier.height(20.dp))
            PreviewCard(settings = settings)

            Spacer(Modifier.height(24.dp))
            VzFooter()
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun defaultModeLabel(mode: DefaultPushMode): String = stringResource(
    when (mode) {
        DefaultPushMode.SYNC -> R.string.push_confirm_sync
        DefaultPushMode.REBUILD -> R.string.push_confirm_rebuild
    }
)

@Composable
private fun postActionLabel(action: PostPushAction): String = stringResource(
    when (action) {
        PostPushAction.COMMIT_ONLY -> R.string.push_settings_post_commit_only
        PostPushAction.COMMIT_AND_PUSH -> R.string.push_settings_post_commit_push
    }
)

@Composable
private fun logStyleLabel(style: LogStyle): String = when (style) {
    LogStyle.TERMINAL -> "Terminal"
    LogStyle.LOG -> "Log"
}

@Composable
private fun subtitleForAction(action: PostPushAction): String = stringResource(
    when (action) {
        PostPushAction.COMMIT_ONLY -> R.string.push_settings_post_commit_only_desc
        PostPushAction.COMMIT_AND_PUSH -> R.string.push_settings_post_commit_push_desc
    }
)

@Composable
private fun TopBar(onBack: () -> Unit, onHelp: () -> Unit) {
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
            text = stringResource(R.string.push_settings_title),
            style = MaterialTheme.typography.titleLarge,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onHelp) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                contentDescription = stringResource(R.string.push_settings_help_action),
                tint = VzOnSurfaceVariant,
            )
        }
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
private fun <T> DropdownRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    currentValue: String,
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { expanded = true }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VzOnSurface,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
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
        Spacer(Modifier.width(8.dp))
        Text(
            text = currentValue,
            style = MaterialTheme.typography.bodySmall,
            color = DarkSecondary,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.width(4.dp))
        Box {
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = null,
                tint = VzOnSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RadioRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
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
        Spacer(Modifier.width(6.dp))
        Column {
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
private fun SwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VzOnSurface,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VzOnSurface,
                checkedTrackColor = DarkPrimary,
                uncheckedThumbColor = VzOnSurfaceVariant,
                uncheckedTrackColor = VzSurfaceVariant,
            ),
        )
    }
}

@Composable
private fun PlaceholderMenu(onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(R.string.push_settings_placeholder_add),
                tint = DarkSecondary,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 420.dp),
        ) {
            Text(
                text = stringResource(R.string.push_settings_placeholders_header),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            )
            PLACEHOLDERS.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = item.code,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = stringResource(item.descriptionRes),
                                style = MaterialTheme.typography.bodySmall,
                                color = VzOnSurfaceVariant,
                            )
                        }
                    },
                    onClick = {
                        onSelect(item.code)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun PreviewCard(settings: PushSettings) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.push_settings_preview_title),
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
        PreviewLine(
            label = stringResource(R.string.push_settings_preview_mode),
            value = defaultModeLabel(settings.defaultMode),
        )
        PreviewLine(
            label = stringResource(R.string.push_settings_preview_action),
            value = postActionLabel(settings.postPushAction),
        )
        PreviewLine(
            label = stringResource(R.string.push_settings_preview_message),
            value = resolvePreview(settings.commitMessageTemplate),
        )
        PreviewLine(
            label = stringResource(R.string.push_settings_preview_log_style),
            value = logStyleLabel(settings.logStyle),
        )
        PreviewLine(
            label = stringResource(R.string.push_settings_preview_auto_log),
            value = stringResource(
                if (settings.autoShowLog) R.string.push_settings_preview_yes
                else R.string.push_settings_preview_no
            ),
        )
    }
}

@Composable
private fun PreviewLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 20.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
            modifier = Modifier.width(140.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun resolvePreview(template: String): String {
    if (template.isBlank()) {
        return stringResource(R.string.push_settings_empty_template)
    }
    val now = LocalDateTime.now()
    return template
        .replace("{date}", now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        .replace("{time}", now.format(DateTimeFormatter.ofPattern("HH:mm")))
        .replace("{folder}", stringResource(R.string.push_settings_preview_folder))
        .replace("{repo}", stringResource(R.string.push_settings_preview_repo))
        .replace("{user}", stringResource(R.string.push_settings_preview_user))
        .replace("{branch}", stringResource(R.string.push_settings_preview_branch))
        .replace("{files}", stringResource(R.string.push_settings_preview_files))
        .replace("{mode}", stringResource(R.string.push_settings_preview_mode))
}