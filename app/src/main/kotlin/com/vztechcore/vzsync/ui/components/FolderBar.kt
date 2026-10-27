package com.vztechcore.vzsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant

@Composable
fun FolderBar(
    folderName: String,
    onMenuClick: () -> Unit,
    onRefreshTree: () -> Unit,
    onChangeFolder: () -> Unit,
    onClearFolder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(VzSurfaceVariant)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = stringResource(R.string.folder_bar_open_tree),
                tint = VzOnSurface,
            )
        }

        Text(
            text = folderName,
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
        )

        Box {
            IconButton(onClick = { menuOpen = true }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.folder_bar_more_options),
                    tint = VzOnSurface,
                )
            }
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.folder_bar_refresh)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            tint = VzOnSurface,
                        )
                    },
                    onClick = {
                        menuOpen = false
                        onRefreshTree()
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.folder_bar_change)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.SwapHoriz,
                            contentDescription = null,
                            tint = VzOnSurface,
                        )
                    },
                    onClick = {
                        menuOpen = false
                        onChangeFolder()
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.folder_bar_close)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = VzOnSurface,
                        )
                    },
                    onClick = {
                        menuOpen = false
                        onClearFolder()
                    },
                )
            }
        }
    }
}