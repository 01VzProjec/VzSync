package com.vztechcore.vzsync.ui.screens.home

import com.vztechcore.vzsync.domain.FolderInfo
import com.vztechcore.vzsync.domain.TreeItem

data class HomeUiState(
    val selectedFolderUri: String? = null,
    val selectedFolderName: String? = null,
    val selectedFolderPath: String? = null,
    val tree: TreeItem? = null,
    val folderInfo: FolderInfo? = null,
    val isLoadingTree: Boolean = false,
    val treeError: String? = null,
    val treeTruncated: Boolean = false,
    val expandedPaths: Set<String> = emptySet(),
    val selectedFile: TreeItem? = null,
    val showFileInfoSheet: Boolean = false,
    val showCloneSheet: Boolean = false,
) {
    val hasFolder: Boolean get() = selectedFolderUri != null
}