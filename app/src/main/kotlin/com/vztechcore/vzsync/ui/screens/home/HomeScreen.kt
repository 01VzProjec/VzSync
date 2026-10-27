package com.vztechcore.vzsync.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.core.util.Formatters
import com.vztechcore.vzsync.domain.FolderInfo
import com.vztechcore.vzsync.domain.TreeItem
import com.vztechcore.vzsync.domain.UserProfile
import com.vztechcore.vzsync.ui.components.FileTreeDrawerContent
import com.vztechcore.vzsync.ui.components.FolderBar
import com.vztechcore.vzsync.ui.components.VzFooter
import com.vztechcore.vzsync.ui.components.VzPrimaryButton
import com.vztechcore.vzsync.ui.components.VzSecondaryButton
import com.vztechcore.vzsync.ui.components.VzTopBar
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurface
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    profile: UserProfile?,
    state: HomeUiState,
    onAvatarClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFolderPicked: (Uri) -> Unit,
    onPushClick: () -> Unit,
    onCloneClick: () -> Unit,
    onRefreshTree: () -> Unit,
    onClearFolder: () -> Unit,
    onToggleExpand: (String) -> Unit,
    onFileClick: (TreeItem) -> Unit,
    onDrawerHelpRequested: () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
    ) { uri: Uri? ->
        if (uri != null) onFolderPicked(uri)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = VzSurface) {
                FileTreeDrawerContent(
                    tree = state.tree,
                    isLoading = state.isLoadingTree,
                    error = state.treeError,
                    expandedPaths = state.expandedPaths,
                    onToggleExpand = onToggleExpand,
                    onFileClick = onFileClick,
                    onRefresh = onRefreshTree,
                    onHelpRequested = onDrawerHelpRequested,
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VzBackground)
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            VzTopBar(
                profile = profile,
                onSettingsClick = onSettingsClick,
                onAvatarClick = onAvatarClick,
            )

            if (state.hasFolder) {
                FolderBar(
                    folderName = state.selectedFolderName.orEmpty(),
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onRefreshTree = onRefreshTree,
                    onChangeFolder = { folderPickerLauncher.launch(null) },
                    onClearFolder = onClearFolder,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AnimatedContent(
                    targetState = state.hasFolder,
                    transitionSpec = {
                        (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.94f))
                            .togetherWith(fadeOut(tween(160)) + scaleOut(tween(160), targetScale = 0.94f))
                    },
                    label = "folderState",
                ) { hasFolder ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (hasFolder) {
                            FolderReadyContent(
                                folderName = state.selectedFolderName.orEmpty(),
                                info = state.folderInfo,
                                isLoading = state.isLoadingTree,
                            )
                        } else {
                            EmptyContent()
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                if (state.hasFolder) {
                    VzPrimaryButton(
                        text = if (state.isLoadingTree) {
                            stringResource(R.string.home_button_analyzing)
                        } else {
                            stringResource(R.string.home_button_send)
                        },
                        isFolderSelected = true,
                        enabled = !state.isLoadingTree,
                        isWorking = state.isLoadingTree,
                        onClick = onPushClick,
                    )
                } else {
                    VzPrimaryButton(
                        text = stringResource(R.string.home_button_select_folder),
                        isFolderSelected = false,
                        enabled = true,
                        onClick = { folderPickerLauncher.launch(null) },
                    )
                    Spacer(Modifier.height(12.dp))
                    VzSecondaryButton(
                        text = stringResource(R.string.home_button_clone),
                        icon = Icons.Rounded.Download,
                        onClick = onCloneClick,
                    )
                }
            }

            VzFooter()
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(VzSurfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.FolderOpen,
            contentDescription = null,
            tint = DarkSecondary,
            modifier = Modifier.size(64.dp),
        )
    }
    Spacer(Modifier.height(24.dp))
    Text(
        text = stringResource(R.string.home_no_folder_title),
        style = MaterialTheme.typography.titleLarge,
        color = VzOnSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.home_no_folder_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = VzOnSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun FolderReadyContent(
    folderName: String,
    info: FolderInfo?,
    isLoading: Boolean,
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(DarkPrimary.copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(56.dp),
                color = DarkSecondary,
                strokeWidth = 4.dp,
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = DarkSecondary,
                modifier = Modifier.size(64.dp),
            )
        }
    }
    Spacer(Modifier.height(20.dp))
    Text(
        text = folderName,
        style = MaterialTheme.typography.titleLarge,
        color = VzOnSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = if (isLoading) {
            stringResource(R.string.home_analyzing_folder)
        } else {
            stringResource(R.string.home_ready_to_send)
        },
        style = MaterialTheme.typography.bodyMedium,
        color = VzOnSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )

    if (info != null && !isLoading) {
        Spacer(Modifier.height(20.dp))
        FolderInfoCard(info = info)
    }
}

@Composable
private fun FolderInfoCard(info: FolderInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VzSurfaceVariant)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            InfoStat(
                label = stringResource(R.string.home_info_modified),
                value = Formatters.shortDate(info.lastModified),
            )
            InfoStat(
                label = stringResource(R.string.home_info_size),
                value = Formatters.bytes(info.totalBytes),
            )
            InfoStat(
                label = stringResource(R.string.home_info_items),
                value = info.totalItems.toString(),
            )
        }

        Text(
            text = pluralStringResource(
                R.plurals.home_file_count,
                info.fileCount,
                info.fileCount,
            ) + " · " + pluralStringResource(
                R.plurals.home_folder_count,
                info.folderCount,
                info.folderCount,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun InfoStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = VzOnSurface,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = VzOnSurfaceVariant,
        )
    }
}