package com.vztechcore.vzsync.ui.screens.clone

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.data.git.CloneFailureReason
import com.vztechcore.vzsync.data.git.CloneProgress
import com.vztechcore.vzsync.data.git.CloneProgressBus
import com.vztechcore.vzsync.data.git.CloneResult
import com.vztechcore.vzsync.data.git.CloneService
import com.vztechcore.vzsync.data.git.CloneServiceEvent
import com.vztechcore.vzsync.data.git.JGitCloner
import com.vztechcore.vzsync.data.git.JGitLogger
import com.vztechcore.vzsync.domain.LogEntry
import com.vztechcore.vzsync.domain.LogLevel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class CloneViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CloneUiState())
    val uiState: StateFlow<CloneUiState> = _uiState.asStateFlow()

    private var cloneJob: Job? = null
    private var currentRepoName: String = ""

    init {
        observeServiceEvents()
        rehydrateFromBus()
    }

    private fun observeServiceEvents() {
        CloneProgressBus.events
            .onEach { event ->
                if (event is CloneServiceEvent.CancelRequested) {
                    // Already handled by requestCancel; keep state consistent.
                }
            }
            .launchIn(viewModelScope)
    }

    private fun rehydrateFromBus() {
        val snapshot = CloneProgressBus.progress.value ?: return
        if (snapshot.percentage >= 100) {
            CloneProgressBus.clear()
            return
        }
        currentRepoName = snapshot.repoName
        _uiState.update {
            it.copy(
                step = CloneStep.CLONING,
                percentage = snapshot.percentage,
                phase = snapshot.phase,
                isIndeterminate = snapshot.isIndeterminate,
                isHidden = false,
            )
        }
    }

    fun reset() {
        cloneJob?.cancel()
        cloneJob = null
        CloneProgressBus.resetCancel()
        _uiState.value = CloneUiState()
    }

    fun onUrlChange(value: String) {
        _uiState.update { it.copy(url = value, errorMessage = null) }
    }

    fun startClone(
        username: String,
        token: String,
        onSuccess: (File, String) -> Unit,
    ) {
        val state = _uiState.value
        if (state.url.isBlank()) return
        if (state.step == CloneStep.CLONING) return

        CloneProgressBus.resetCancel()
        currentRepoName = extractRepoName(state.url)

        _uiState.update {
            it.copy(
                step = CloneStep.CLONING,
                logs = emptyList(),
                errorMessage = null,
                errorReason = null,
                percentage = 0,
                phase = "",
                isIndeterminate = false,
                isHidden = false,
            )
        }

        CloneService.start(getApplication(), currentRepoName)

        cloneJob = viewModelScope.launch {
            val buffer = mutableListOf<LogEntry>()

            fun appendLog(level: LogLevel, message: String) {
                buffer += LogEntry(level, message)
                _uiState.update { it.copy(logs = buffer.toList()) }
            }

            fun fail(message: String, reason: CloneFailureReason) {
                appendLog(LogLevel.ERROR, message)
                CloneProgressBus.stop()
                CloneService.stop(getApplication())
                _uiState.update {
                    it.copy(
                        step = CloneStep.ERROR,
                        errorMessage = message,
                        errorReason = reason,
                    )
                }
            }

            if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
                fail(
                    getApplication<Application>().getString(
                        R.string.clone_vm_error_storage_permission
                    ),
                    CloneFailureReason.STORAGE_PERMISSION_MISSING,
                )
                return@launch
            }

            if (username.isBlank() || token.isBlank()) {
                fail(
                    getApplication<Application>().getString(
                        R.string.clone_vm_error_not_connected
                    ),
                    CloneFailureReason.AUTH_FAILED,
                )
                return@launch
            }

            val baseDir = File(Environment.getExternalStorageDirectory(), "VzSync/Repos")
            val logger = JGitLogger { level, message -> appendLog(level, message) }
            val cloner = JGitCloner(logger)

            val result = cloner.clone(
                url = state.url,
                username = username,
                token = token,
                baseDir = baseDir,
                cancelRequested = CloneProgressBus.cancelRequested,
                onProgress = { percentage, phase, indeterminate ->
                    _uiState.update {
                        it.copy(
                            percentage = percentage,
                            phase = phase,
                            isIndeterminate = indeterminate,
                        )
                    }
                    CloneProgressBus.emitProgress(
                        CloneProgress(
                            repoName = currentRepoName,
                            percentage = percentage,
                            phase = phase,
                            isIndeterminate = indeterminate,
                        )
                    )
                },
            )

            when (result) {
                is CloneResult.Success -> {
                    CloneProgressBus.emitProgress(
                        CloneProgress(
                            repoName = result.repoName,
                            percentage = 100,
                            phase = "Concluído",
                            isIndeterminate = false,
                        )
                    )
                    CloneProgressBus.stop()
                    CloneService.stop(getApplication())
                    _uiState.update {
                        it.copy(
                            step = CloneStep.SUCCESS,
                            resultPath = result.file.absolutePath,
                            resultRepoName = result.repoFullName,
                            percentage = 100,
                        )
                    }
                    onSuccess(result.file, result.repoName)
                }
                is CloneResult.Failure -> {
                    if (result.reason == CloneFailureReason.CANCELLED) {
                        CloneProgressBus.stop()
                        CloneService.stop(getApplication())
                        _uiState.update {
                            it.copy(
                                step = CloneStep.INPUT,
                                logs = emptyList(),
                                percentage = 0,
                                phase = "",
                                isHidden = false,
                            )
                        }
                    } else {
                        fail(result.message, result.reason)
                    }
                }
            }
        }
    }

    fun hideSheet() {
        _uiState.update { it.copy(isHidden = true) }
    }

    fun showSheet() {
        _uiState.update { it.copy(isHidden = false) }
    }

    fun cancelClone() {
        CloneProgressBus.requestCancel()
    }

    fun backToInput() {
        _uiState.update {
            it.copy(
                step = CloneStep.INPUT,
                errorMessage = null,
                errorReason = null,
                logs = emptyList(),
                percentage = 0,
                phase = "",
                isHidden = false,
            )
        }
    }

    fun openStorageSettings() {
        val context = getApplication<Application>()
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
            }
        }
    }

    fun openXiaomiBatterySettings() {
        val context = getApplication<Application>()
        val intents = listOf(
            Intent().apply {
                component = android.content.ComponentName(
                    "com.miui.powerkeeper",
                    "com.miui.powerkeeper.ui.HiddenAppsConfigActivity",
                )
                putExtra("package_name", context.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent().apply {
                component = android.content.ComponentName(
                    "com.miui.powerkeeper",
                    "com.miui.powerkeeper.ui.HiddenAppsContainerManagementActivity",
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
        for (intent in intents) {
            try {
                context.startActivity(intent)
                return
            } catch (_: Exception) {
                continue
            }
        }
    }

    fun dismissXiaomiDialog() {
        _uiState.update { it.copy(showXiaomiDialog = false) }
    }

    fun requestXiaomiDialog() {
        _uiState.update { it.copy(showXiaomiDialog = true) }
    }

    fun dismiss() {
        if (_uiState.value.step != CloneStep.CLONING) {
            CloneProgressBus.clear()
        }
        cloneJob?.cancel()
        cloneJob = null
        _uiState.value = CloneUiState()
    }

    private fun extractRepoName(url: String): String {
        return url.trim()
            .trimEnd('/')
            .removeSuffix(".git")
            .substringAfterLast('/')
            .ifBlank { "repo" }
    }
}