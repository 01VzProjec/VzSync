package com.vztechcore.vzsync.domain

data class TreeItem(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val sizeBytes: Long = 0L,
    val lastModified: Long = 0L,
    val children: List<TreeItem> = emptyList(),
)