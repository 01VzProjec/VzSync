package com.vztechcore.vzsync.data.files

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.vztechcore.vzsync.domain.FolderInfo
import com.vztechcore.vzsync.domain.TreeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

sealed interface TreeLoadResult {
    data class Success(
        val tree: TreeItem,
        val truncated: Boolean,
        val info: FolderInfo,
    ) : TreeLoadResult
    data class Failure(val message: String) : TreeLoadResult
}

object FileTreeLoader {

    private const val MAX_NODES = 3000
    private const val MAX_DEPTH = 15

    private val SKIP_NAMES = setOf(
        ".git", "build", "node_modules", ".gradle", ".idea",
        "__pycache__", ".cache", "target", "dist", "out",
    )
    private val KEEP_HIDDEN = setOf(
        ".gitignore", ".env", ".editorconfig", ".gitattributes",
    )

    suspend fun loadTree(context: Context, rootUri: Uri): TreeLoadResult =
        withContext(Dispatchers.IO) {
            try {
                val root = DocumentFile.fromTreeUri(context, rootUri)
                    ?: return@withContext TreeLoadResult.Failure("Falha ao abrir a pasta.")

                var count = 0
                var truncated = false

                fun build(file: DocumentFile, depth: Int): TreeItem? {
                    if (count >= MAX_NODES || depth > MAX_DEPTH) {
                        truncated = true
                        return null
                    }
                    count++

                    val name = file.name ?: return null
                    val isDir = file.isDirectory
                    val size = if (isDir) 0L else runCatching { file.length() }.getOrDefault(0L)
                    val modified = runCatching { file.lastModified() }.getOrDefault(0L)

                    val children = if (isDir) {
                        runCatching { file.listFiles().toList() }
                            .getOrDefault(emptyList())
                            .filter { shouldInclude(it.name) }
                            .mapNotNull { build(it, depth + 1) }
                            .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                    } else {
                        emptyList()
                    }

                    return TreeItem(
                        path = file.uri.toString(),
                        name = name,
                        isDirectory = isDir,
                        sizeBytes = size.coerceAtLeast(0L),
                        lastModified = modified,
                        children = children,
                    )
                }

                val tree = build(root, 0)
                    ?: return@withContext TreeLoadResult.Failure("Não foi possível ler a pasta.")

                TreeLoadResult.Success(
                    tree = tree,
                    truncated = truncated,
                    info = computeInfo(tree),
                )
            } catch (e: SecurityException) {
                TreeLoadResult.Failure("Sem permissão para ler a pasta. Escolha novamente.")
            } catch (e: Exception) {
                TreeLoadResult.Failure(e.message ?: "Falha ao carregar a árvore de arquivos.")
            }
        }

    suspend fun loadTreeFromFile(rootFile: File): TreeLoadResult =
        withContext(Dispatchers.IO) {
            try {
                if (!rootFile.exists() || !rootFile.isDirectory) {
                    return@withContext TreeLoadResult.Failure("A pasta não existe.")
                }

                var count = 0
                var truncated = false

                fun build(file: File, depth: Int): TreeItem? {
                    if (count >= MAX_NODES || depth > MAX_DEPTH) {
                        truncated = true
                        return null
                    }
                    count++

                    val isDir = file.isDirectory
                    val children = if (isDir) {
                        (file.listFiles()?.toList() ?: emptyList())
                            .filter { shouldInclude(it.name) }
                            .mapNotNull { build(it, depth + 1) }
                            .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                    } else {
                        emptyList()
                    }

                    return TreeItem(
                        path = file.toURI().toString(),
                        name = file.name,
                        isDirectory = isDir,
                        sizeBytes = if (isDir) 0L else runCatching { file.length() }.getOrDefault(0L),
                        lastModified = runCatching { file.lastModified() }.getOrDefault(0L),
                        children = children,
                    )
                }

                val tree = build(rootFile, 0)
                    ?: return@withContext TreeLoadResult.Failure("Não foi possível ler a pasta.")

                TreeLoadResult.Success(
                    tree = tree,
                    truncated = truncated,
                    info = computeInfo(tree),
                )
            } catch (e: SecurityException) {
                TreeLoadResult.Failure("Sem permissão para ler a pasta.")
            } catch (e: Exception) {
                TreeLoadResult.Failure(e.message ?: "Falha ao carregar a árvore de arquivos.")
            }
        }

    fun initialExpanded(tree: TreeItem): Set<String> {
        val set = mutableSetOf<String>()
        expandAuto(tree, set)
        return set
    }

    fun expandAuto(item: TreeItem, expanded: MutableSet<String>) {
        expanded.add(item.path)
        if (item.children.size == 1) {
            val only = item.children[0]
            if (only.isDirectory) {
                expandAuto(only, expanded)
            }
        }
    }

    fun collapseRecursive(item: TreeItem, expanded: MutableSet<String>) {
        expanded.remove(item.path)
        item.children.forEach { collapseRecursive(it, expanded) }
    }

    fun findItem(tree: TreeItem?, path: String): TreeItem? {
        if (tree == null) return null
        if (tree.path == path) return tree
        for (child in tree.children) {
            val found = findItem(child, path)
            if (found != null) return found
        }
        return null
    }

    private fun computeInfo(tree: TreeItem): FolderInfo {
        var files = 0
        var folders = 0
        var bytes = 0L

        fun walk(item: TreeItem) {
            item.children.forEach { child ->
                if (child.isDirectory) {
                    folders++
                } else {
                    files++
                    bytes += child.sizeBytes.coerceAtLeast(0L)
                }
                walk(child)
            }
        }
        walk(tree)

        return FolderInfo(
            fileCount = files,
            folderCount = folders,
            totalBytes = bytes,
            lastModified = tree.lastModified,
        )
    }

    private fun shouldInclude(name: String?): Boolean {
        if (name.isNullOrBlank()) return false
        if (name in SKIP_NAMES) return false
        if (name.startsWith(".") && name !in KEEP_HIDDEN) return false
        return true
    }
}