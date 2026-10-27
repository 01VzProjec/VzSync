package com.vztechcore.vzsync.ui.screens.licenses

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.content.licenses.Libraries
import com.vztechcore.vzsync.domain.licenses.LicenseCategory
import com.vztechcore.vzsync.domain.licenses.OpenSourceLibrary
import com.vztechcore.vzsync.ui.components.VzFooter
import com.vztechcore.vzsync.ui.components.VzSecondaryTopBar
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant

@Composable
fun LicensesScreen(
    onBack: () -> Unit,
) {
    var selected by remember { mutableStateOf<OpenSourceLibrary?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VzBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VzSecondaryTopBar(
            title = stringResource(R.string.libraries_title),
            onBack = onBack,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Header() }
            item { Spacer(Modifier.height(4.dp)) }

            items(Libraries.all, key = { it.name }) { library ->
                LibraryRow(
                    library = library,
                    onClick = { selected = library },
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                VzFooter()
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    selected?.let { library ->
        LibraryDetailSheet(
            library = library,
            onDismiss = { selected = null },
        )
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.libraries_header_title),
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = stringResource(
                R.string.libraries_header_subtitle,
                Libraries.all.size,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )

        Spacer(Modifier.height(4.dp))

        LegendRow(LicenseCategory.LIBERAL, stringResource(R.string.libraries_legend_liberal))
        LegendRow(LicenseCategory.MODERATE, stringResource(R.string.libraries_legend_moderate))
        LegendRow(LicenseCategory.STRICT, stringResource(R.string.libraries_legend_strict))

        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.libraries_hint),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}

@Composable
private fun LegendRow(category: LicenseCategory, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(licenseColor(category)),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurface,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun LibraryRow(
    library: OpenSourceLibrary,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = library.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = VzOnSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                LicenseBadge(library = library)
            }
            Text(
                text = stringResource(library.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = VzOnSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}