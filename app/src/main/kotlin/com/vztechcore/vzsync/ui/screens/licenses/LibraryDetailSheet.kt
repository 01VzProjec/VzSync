package com.vztechcore.vzsync.ui.screens.licenses

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.domain.licenses.OpenSourceLibrary
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurface
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryDetailSheet(
    library: OpenSourceLibrary,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    val copiedLabel = stringResource(R.string.libraries_copied)
    val linkErrorLabel = stringResource(R.string.libraries_link_error)

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, copiedLabel, Toast.LENGTH_SHORT).show()
    }

    fun openInBrowser(url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
            Toast.makeText(context, linkErrorLabel, Toast.LENGTH_SHORT).show()
        }
    }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = library.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = VzOnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                LicenseBadge(library = library)
            }

            Text(
                text = stringResource(library.descriptionRes),
                style = MaterialTheme.typography.bodyMedium,
                color = VzOnSurface,
            )

            Text(
                text = stringResource(
                    R.string.libraries_detail_used_for,
                    stringResource(library.usedForRes),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurfaceVariant,
            )

            LinkRow(
                label = stringResource(R.string.libraries_detail_project_link),
                url = library.projectUrl,
                copyContentDescription = stringResource(R.string.libraries_detail_copy_link),
                openContentDescription = stringResource(R.string.libraries_detail_open_link),
                onCopy = { copyToClipboard(library.name, library.projectUrl) },
                onOpen = { openInBrowser(library.projectUrl) },
            )

            LinkRow(
                label = stringResource(R.string.libraries_detail_license_text),
                url = library.licenseUrl,
                copyContentDescription = stringResource(R.string.libraries_detail_copy_link),
                openContentDescription = stringResource(R.string.libraries_detail_open_link),
                onCopy = { copyToClipboard(library.name, library.licenseUrl) },
                onOpen = { openInBrowser(library.licenseUrl) },
            )

            Button(
                onClick = onDismiss,
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
                    text = stringResource(R.string.libraries_detail_close),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun LinkRow(
    label: String,
    url: String,
    copyContentDescription: String,
    openContentDescription: String,
    onCopy: () -> Unit,
    onOpen: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VzSurfaceVariant)
            .padding(start = 12.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                color = VzOnSurface,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = copyContentDescription,
                    tint = VzOnSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
            IconButton(
                onClick = onOpen,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.OpenInNew,
                    contentDescription = openContentDescription,
                    tint = VzOnSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}