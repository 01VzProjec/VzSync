package com.vztechcore.vzsync.ui.screens.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.content.help.HelpContent
import com.vztechcore.vzsync.domain.help.HelpBlock
import com.vztechcore.vzsync.domain.help.HelpSection
import com.vztechcore.vzsync.ui.components.VzFooter
import com.vztechcore.vzsync.ui.theme.DarkError
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

private val TipColor = Color(0xFF4ADE80)
private val WarningColor = Color(0xFFF59E0B)
private val DangerColor = DarkError

@Composable
fun HelpScreen(
    uiState: HelpUiState,
    onQueryChange: (String) -> Unit,
    onToggleSection: (HelpSection) -> Unit,
    onAutoScrollConsumed: () -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.autoScrollTo) {
        val target = uiState.autoScrollTo ?: return@LaunchedEffect
        val index = uiState.filteredSections.indexOf(target)
        if (index >= 0) {
            listState.animateScrollToItem(index + 1)
        }
        onAutoScrollConsumed()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VzBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        TopBar(onBack = onBack)

        SearchField(
            query = uiState.query,
            onQueryChange = onQueryChange,
        )

        if (uiState.query.isNotBlank() && uiState.filteredSections.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.help_search_empty, uiState.query),
                    style = MaterialTheme.typography.bodyMedium,
                    color = VzOnSurfaceVariant,
                )
            }
            return@Column
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "header") {
                HelpHeader()
            }

            items(uiState.filteredSections, key = { it.name }) { section ->
                SectionCard(
                    section = section,
                    isExpanded = section in uiState.expandedSections,
                    onClick = { onToggleSection(section) },
                )
            }

            item(key = "footer") {
                Spacer(Modifier.height(16.dp))
                VzFooter()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
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
            text = stringResource(R.string.settings_help),
            style = MaterialTheme.typography.titleLarge,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.help_search_placeholder)) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = VzOnSurfaceVariant,
            )
        },
        trailingIcon = if (query.isNotBlank()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.help_search_clear),
                        tint = VzOnSurfaceVariant,
                    )
                }
            }
        } else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DarkPrimary,
            unfocusedBorderColor = VzSurfaceVariant,
            focusedPlaceholderColor = VzOnSurfaceVariant,
            unfocusedPlaceholderColor = VzOnSurfaceVariant,
            cursorColor = DarkPrimary,
            focusedTextColor = VzOnSurface,
            unfocusedTextColor = VzOnSurface,
        ),
    )
}

@Composable
private fun HelpHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.help_header_title),
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = stringResource(R.string.help_header_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}

@Composable
private fun SectionCard(
    section: HelpSection,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(180),
        label = "chevron",
    )

    val borderColor = DarkSecondary.copy(alpha = 0.35f)
    val shape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(width = 1.dp, color = borderColor, shape = shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = section.icon,
                contentDescription = null,
                tint = DarkSecondary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(section.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = VzOnSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(section.subtitleRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = null,
                tint = VzOnSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(rotation),
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(tween(200)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(180)) + fadeOut(tween(180)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                HelpContent.blocksFor(section).forEach { block ->
                    HelpBlockView(block)
                }
            }
        }
    }
}

@Composable
private fun HelpBlockView(block: HelpBlock) {
    when (block) {
        is HelpBlock.Heading -> Text(
            text = stringResource(block.textRes),
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )

        is HelpBlock.Paragraph -> Text(
            text = stringResource(block.textRes),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )

        is HelpBlock.Bullet -> Row {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(block.textRes),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }

        is HelpBlock.Code -> Text(
            text = stringResource(block.textRes),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurface,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF000000))
                .padding(10.dp),
        )

        is HelpBlock.Tip -> CalloutBox(
            text = stringResource(block.textRes),
            color = TipColor,
            label = stringResource(R.string.help_callout_tip),
        )

        is HelpBlock.Warning -> CalloutBox(
            text = stringResource(block.textRes),
            color = WarningColor,
            label = stringResource(R.string.help_callout_warning),
        )

        is HelpBlock.Danger -> CalloutBox(
            text = stringResource(block.textRes),
            color = DangerColor,
            label = stringResource(R.string.help_callout_danger),
        )
    }
}

@Composable
private fun CalloutBox(text: String, color: Color, label: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
        )
    }
}