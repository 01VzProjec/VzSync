package com.vztechcore.vzsync.data.files

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract

/**
 * Attempts to reconstruct a human-readable storage path from a SAF tree URI.
 *
 * The Storage Access Framework does NOT expose real file paths. A tree URI
 * like content://com.android.externalstorage.documents/tree/primary%3AVzSync
 * can be decoded back to /storage/emulated/0/VzSync for local storage and SD
 * cards. Cloud providers (Drive, etc.) cannot be resolved this way and return
 * null — callers must fall back to the folder display name.
 */
object DocumentPathResolver {

    fun resolve(context: Context, treeUri: Uri): String? {
        return try {
            val docId = DocumentsContract.getTreeDocumentId(treeUri)
            val parts = docId.split(":", limit = 2)
            if (parts.size != 2) return null

            val volume = parts[0]
            val relative = parts[1]

            val base = when {
                volume.equals("primary", ignoreCase = true) ->
                    Environment.getExternalStorageDirectory().absolutePath
                // SD card volumes are like "1A2B-3C4D" (no "primary:")
                volume.isNotBlank() -> "/storage/$volume"
                else -> return null
            }

            if (relative.isBlank()) base else "$base/$relative"
        } catch (_: Exception) {
            null
        }
    }
}