package com.vztechcore.vzsync.ui.screens.about

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.ui.components.VzFooter
import com.vztechcore.vzsync.ui.components.VzSecondaryTopBar
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

private const val APP_VERSION = "1.0.0"

@Composable
fun AboutScreen(
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VzBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VzSecondaryTopBar(
            title = stringResource(R.string.about_title),
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))

            LogoPlaceholder()

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = VzOnSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.about_version_format, APP_VERSION),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            StorySection()

            Spacer(Modifier.height(28.dp))

            InfoSection()

            Spacer(Modifier.height(24.dp))
            VzFooter()
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LogoPlaceholder() {
    // Circular placeholder reserved for the future app logo.
    // When the logo is ready, replace the Box content with an Image.
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(VzSurfaceVariant.copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center,
    ) {
        // TODO: add the VzSync logo here when available.
    }
}

@Composable
private fun StorySection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.about_story_title),
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            text = stringResource(R.string.about_story_p1),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )

        Text(
            text = stringResource(R.string.about_story_p2),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )

        Text(
            text = stringResource(R.string.about_story_p3),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )

        Text(
            text = stringResource(R.string.about_story_p4),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}

@Composable
private fun InfoSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.about_info_title),
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
        InfoRow(
            label = stringResource(R.string.about_info_version),
            value = APP_VERSION,
        )
        InfoRow(
            label = stringResource(R.string.about_info_author),
            value = "Vtzin x",
        )
        InfoRow(
            label = stringResource(R.string.about_info_lab),
            value = "VzTechCore",
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurface,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
        )
    }
}