package com.vztechcore.vzsync.ui.screens.onboarding

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.ui.components.VzPrimaryButton
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VzBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(DarkPrimary.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Vz",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DarkSecondary,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.welcome_title),
                style = MaterialTheme.typography.headlineMedium,
                color = VzOnSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = VzOnSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(40.dp))

            FeatureRow(
                icon = Icons.Rounded.Key,
                title = stringResource(R.string.welcome_feature_1_title),
                description = stringResource(R.string.welcome_feature_1_desc),
            )
            Spacer(Modifier.height(16.dp))
            FeatureRow(
                icon = Icons.Rounded.Download,
                title = stringResource(R.string.welcome_feature_2_title),
                description = stringResource(R.string.welcome_feature_2_desc),
            )
            Spacer(Modifier.height(16.dp))
            FeatureRow(
                icon = Icons.Rounded.CloudUpload,
                title = stringResource(R.string.welcome_feature_3_title),
                description = stringResource(R.string.welcome_feature_3_desc),
            )

            Spacer(Modifier.height(32.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            VzPrimaryButton(
                text = stringResource(R.string.welcome_start),
                isFolderSelected = true,
                enabled = true,
                onClick = onStart,
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VzSurfaceVariant.copy(alpha = 0.5f))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DarkPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DarkSecondary,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
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
}