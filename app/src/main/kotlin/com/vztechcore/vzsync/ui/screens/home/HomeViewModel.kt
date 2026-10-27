package com.vztechcore.vzsync.ui.screens.home

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vztechcore.vzsync.data.files.DocumentPathResolver
import com.vztechcore.vzsync.data.files.FileTreeLoader
import com.vztechcore.vzsync.data.files.TreeLoadResult
import com.vztechcore.vzsync.data.prefs.SecurePrefs
import com.vztechcore.vzsync.domain.TreeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = SecurePrefs.get(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        restoreFolder()
    }

    private fun restoreFolder() {
        val uri = prefs.getFolderUri() ?: return
        val name = prefs.getFolderName() ?: return
        val path = prefs.getFolderPath() ?: name

        // Cloned folders use file:// scheme (created by the clone pipeline).
        if (uri.startsWith("file:")) {
            val file = Uri.parse(uri).path?.let { File(it) } ?: return
            if (!file.exists() || !file.isDirectory) {
                prefs.clearFolder()
                return
            }
            _uiState.update {
                it.copy(
                    selectedFolderUri = uri,
                    selectedFolderName = name,
                    selectedFolderPath = path,
                )
            }
            loadTreeFromFile(file)
            return
        }

        val hasPermission = getApplication<Application>().contentResolver
            .persistedUriPermissions
            .any { it.uri == Uri.parse(uri) && it.isReadPermission }

        if (!hasPermission) {
            prefs.clearFolder()
            return
        }

        _uiState.update {
            it.copy(
                selectedFolderUri = uri,
                selectedFolderName = name,
                selectedFolderPath = path,
            )
        }
        loadTree(Uri.parse(uri))
    }

    fun onFolderSelected(uri: Uri) {
        val app = getApplication<Application>()
        try {
            app.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
        } catch (_: SecurityException) {
        }

        val name = runCatching { DocumentFile.fromTreeUri(app, uri)?.name }
            .getOrNull() ?: "Pasta selecionada"

        val resolvedPath = DocumentPathResolver.resolve(app, uri) ?: name

        prefs.saveFolder(uri.toString(), name, resolvedPath)
        _uiState.update {
            it.copy(
                selectedFolderUri = uri.toString(),
                selectedFolderName = name,
                selectedFolderPath = resolvedPath,
                expandedPaths = emptySet(),
                tree = null,
                folderInfo = null,
                treeError = null,
                selectedFile = null,
                showFileInfoSheet = false,
            )
        }
        loadTree(uri)
    }

    /**
     * Called when a folder was created by the clone pipeline. The folder has
     * no SAF URI, only a real file path. Uses file:// pseudo-URI internally.
     */
    fun onClonedFolderReady(file: File) {
        if (!file.exists() || !file.isDirectory) return
        val pseudoUri = file.toURI().toString()
        val name = file.name
        val path = file.absolutePath

        prefs.saveFolder(pseudoUri, name, path)
        _uiState.update {
            it.copy(
                selectedFolderUri = pseudoUri,
                selectedFolderName = name,
                selectedFolderPath = path,
                expandedPaths = emptySet(),
                tree = null,
                folderInfo = null,
                treeError = null,
                selectedFile = null,
                showFileInfoSheet = false,
            )
        }
        loadTreeFromFile(file)
    }

    fun clearFolder() {
        prefs.clearFolder()
        _uiState.update {
            it.copy(
                selectedFolderUri = null,
                selectedFolderName = null,
                selectedFolderPath = null,
                tree = null,
                folderInfo = null,
                expandedPaths = emptySet(),
                treeError = null,
                selectedFile = null,
                showFileInfoSheet = false,
            )
        }
    }

    fun refreshTree() {
        val uri = _uiState.value.selectedFolderUri ?: return
        if (uri.startsWith("file:")) {
            val file = Uri.parse(uri).path?.let { File(it) } ?: return
            loadTreeFromFile(file)
        } else {
            loadTree(Uri.parse(uri))
        }
    }

    fun toggleExpand(path: String) {
        _uiState.update { state ->
            val tree = state.tree ?: return@update state
            val item = FileTreeLoader.findItem(tree, path) ?: return@update state

            val set = state.expandedPaths.toMutableSet()
            if (path in set) {
                FileTreeLoader.collapseRecursive(item, set)
            } else {
                FileTreeLoader.expandAuto(item, set)
            }
            state.copy(expandedPaths = set)
        }
    }

    fun onFileClick(item: TreeItem) {
        _uiState.update {
            it.copy(selectedFile = item, showFileInfoSheet = false)
        }
    }

    fun dismissFileBar() {
        _uiState.update {
            it.copy(selectedFile = null, showFileInfoSheet = false)
        }
    }

    fun showFileInfoSheet() {
        _uiState.update { it.copy(showFileInfoSheet = true) }
    }

    fun hideFileInfoSheet() {
        _uiState.update { it.copy(showFileInfoSheet = false) }
    }

    fun openSelectedFileInOtherApp() {
        val item = _uiState.value.selectedFile ?: return
        val context = getApplication<Application>()
        val uri = Uri.parse(item.path)

        // Files in cloned folders use file:// and would require FileProvider.
        // Skip gracefully — v2 will wire a FileProvider for these.
        if (uri.scheme == "file") return

        val mime = context.contentResolver.getType(uri)
            ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                item.name.substringAfterLast('.', "")
            )
            ?: "*/*"

        if (tryLaunchChooser(context, uri, mime, Intent.ACTION_VIEW)) return
        if (tryLaunchChooser(context, uri, mime, Intent.ACTION_SEND)) return
    }

    private fun tryLaunchChooser(
        context: Application,
        uri: Uri,
        mime: String,
        action: String,
    ): Boolean {
        if (uri.scheme == "file") return false
        return try {
            val target = Intent(action).apply {
                if (action == Intent.ACTION_VIEW) {
                    setDataAndType(uri, mime)
                } else {
                    type = mime
                    putExtra(Intent.EXTRA_STREAM, uri)
                }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(target, "Abrir com...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            _uiState.update { it.copy(showFileInfoSheet = false) }
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    fun openCloneSheet() {
        _uiState.update { it.copy(showCloneSheet = true) }
    }

    fun dismissCloneSheet() {
        _uiState.update { it.copy(showCloneSheet = false) }
    }

    private fun loadTree(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTree = true, treeError = null) }
            when (val result = FileTreeLoader.loadTree(getApplication(), uri)) {
                is TreeLoadResult.Success -> _uiState.update {
                    it.copy(
                        isLoadingTree = false,
                        tree = result.tree,
                        folderInfo = result.info,
                        treeTruncated = result.truncated,
                        expandedPaths = FileTreeLoader.initialExpanded(result.tree),
                        treeError = null,
                    )
                }
                is TreeLoadResult.Failure -> _uiState.update {
                    it.copy(
                        isLoadingTree = false,
                        tree = null,
                        folderInfo = null,
                        treeError = result.message,
                    )
                }
            }
        }
    }

    private fun loadTreeFromFile(file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTree = true, treeError = null) }
            when (val result = FileTreeLoader.loadTreeFromFile(file)) {
                is TreeLoadResult.Success -> _uiState.update {
                    it.copy(
                        isLoadingTree = false,
                        tree = result.tree,
                        folderInfo = result.info,
                        treeTruncated = result.truncated,
                        expandedPaths = FileTreeLoader.initialExpanded(result.tree),
                        treeError = null,
                    )
                }
                is TreeLoadResult.Failure -> _uiState.update {
                    it.copy(
                        isLoadingTree = false,
                        tree = null,
                        folderInfo = null,
                        treeError = result.message,
                    )
                }
            }
        }
    }
}