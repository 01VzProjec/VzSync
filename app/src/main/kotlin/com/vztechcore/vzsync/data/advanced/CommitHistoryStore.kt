package com.vztechcore.vzsync.data.advanced

import android.content.Context
import android.content.SharedPreferences
import com.vztechcore.vzsync.domain.advanced.CommitInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CommitHistoryStore private constructor(private val prefs: SharedPreferences) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun getAll(): List<CommitInfo> {
        val raw = prefs.getString(KEY_COMMITS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<CommitInfo>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getAllRepos(): List<String> =
        getAll().map { it.repoFullName }.distinct()

    fun getForRepo(repoFullName: String): List<CommitInfo> =
        getAll().filter { it.repoFullName == repoFullName }

    fun add(commit: CommitInfo) {
        val current = getAll().toMutableList()
        current.add(0, commit)
        val trimmed = current.take(MAX_COMMITS)
        prefs.edit()
            .putString(KEY_COMMITS, json.encodeToString(trimmed))
            .apply()
    }

    fun removeLastForRepo(repoFullName: String): CommitInfo? {
        val all = getAll().toMutableList()
        val index = all.indexOfFirst { it.repoFullName == repoFullName }
        if (index < 0) return null
        val removed = all.removeAt(index)
        prefs.edit()
            .putString(KEY_COMMITS, json.encodeToString(all))
            .apply()
        return removed
    }

    /**
     * Best-effort lookup of the repo most likely to be relevant for a folder.
     * Matches the folder display path by suffix (e.g. "VzSync" matches "/.../VzSync").
     */
    fun getMostRecentRepoForFolder(folderHint: String): String? {
        if (folderHint.isBlank()) return null
        return getAll().firstOrNull {
            it.folderDisplayPath.endsWith(folderHint, ignoreCase = true)
        }?.repoFullName
    }

    fun getMostRecentRepo(): String? =
        getAll().firstOrNull()?.repoFullName

    fun clear() {
        prefs.edit().remove(KEY_COMMITS).apply()
    }

    fun getCurrentBranch(): String =
        prefs.getString(KEY_CURRENT_BRANCH, "main") ?: "main"

    fun setCurrentBranch(branch: String) {
        prefs.edit().putString(KEY_CURRENT_BRANCH, branch).apply()
    }

    companion object {
        private const val PREFS_NAME = "vz_commit_history"
        private const val KEY_COMMITS = "commits"
        private const val KEY_CURRENT_BRANCH = "current_branch"
        private const val MAX_COMMITS = 100

        @Volatile
        private var instance: CommitHistoryStore? = null

        fun get(context: Context): CommitHistoryStore {
            return instance ?: synchronized(this) {
                instance ?: CommitHistoryStore(
                    context.applicationContext
                        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                ).also { instance = it }
            }
        }
    }
}