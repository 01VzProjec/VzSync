package com.vztechcore.vzsync.domain

data class FolderInfo(
    val fileCount: Int,
    val folderCount: Int,
    val totalBytes: Long,
    val lastModified: Long,
) {
    val totalItems: Int get() = fileCount + folderCount
}