package com.vztechcore.vzsync.ui.screens.push.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.Repo
import com.vztechcore.vzsync.ui.screens.push.RepoPickCard
import com.vztechcore.vzsync.ui.screens.push.StepHeader
import com.vztechcore.vzsync.ui.theme.DarkError
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant

@Composable
internal fun PushSelectRepoStep(
    repos: List<Repo>,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onSelect: (Repo) -> Unit,
    onRetry: () -> Unit,
) {
    StepHeader(
        title = stringResource(R.string.push_select_title),
        onBack = onBack,
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = stringResource(R.string.push_select_subtitle),
        style = MaterialTheme.typography.bodySmall,
        color = VzOnSurfaceVariant,
    )
    Spacer(Modifier.height(16.dp))

    when {
        isLoading -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = DarkSecondary)
        }

        error != null -> Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkError.copy(alpha = 0.12f))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = DarkError,
            )
            Text(
                text = stringResource(R.string.push_select_retry),
                style = MaterialTheme.typography.bodySmall,
                color = DarkSecondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onRetry),
            )
        }

        repos.isEmpty() -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.push_select_empty),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }

        else -> Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repos.forEach { repo ->
                RepoPickCard(repo = repo, onClick = { onSelect(repo) })
            }
        }
    }
}