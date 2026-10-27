package com.vztechcore.vzsync.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatters {

    fun bytes(bytes: Long): String {
        if (bytes <= 0L) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format(Locale.US, "%.2f GB", gb)
            mb >= 1.0 -> String.format(Locale.US, "%.2f MB", mb)
            kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
            else -> "$bytes B"
        }
    }

    fun shortDate(epochMs: Long): String {
        if (epochMs <= 0L) return "—"
        val instant = Instant.ofEpochMilli(epochMs)
        val formatter = DateTimeFormatter
            .ofPattern("dd/MM/yyyy", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    /**
     * Shortens a storage path to "/.../{lastSegment}".
     * "/storage/emulated/0/VzSync" -> "/.../VzSync"
     * "" -> "" (caller should hide the prefix)
     */
    fun shortFolderPath(fullPath: String): String {
        if (fullPath.isBlank()) return ""
        val trimmed = fullPath.trimEnd('/')
        val name = trimmed.substringAfterLast('/')
        return if (name.isNotBlank()) "/.../$name" else trimmed
    }
}