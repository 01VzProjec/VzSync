package com.vztechcore.vzsync.ui.screens.push

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.core.util.Formatters
import com.vztechcore.vzsync.core.util.StringResolver
import com.vztechcore.vzsync.data.advanced.CommitHistoryStore
import com.vztechcore.vzsync.data.git.FailureReason
import com.vztechcore.vzsync.data.git.GitOperationResult
import com.vztechcore.vzsync.data.git.JGitLogger
import com.vztechcore.vzsync.data.git.JGitPipeline
import com.vztechcore.vzsync.data.git.JGitPushRequest
import com.vztechcore.vzsync.data.prefs.SecurePrefs
import com.vztechcore.vzsync.data.repos.CreateRepoResult
import com.vztechcore.vzsync.data.repos.RepoRepository
import com.vztechcore.vzsync.domain.LogEntry
import com.vztechcore.vzsync.domain.LogLevel
import com.vztechcore.vzsync.domain.Repo
import com.vztechcore.vzsync.domain.advanced.CommitInfo
import com.vztechcore.vzsync.domain.advanced.CommitMode
import com.vztechcore.vzsync.domain.push.DefaultPushMode
import com.vztechcore.vzsync.domain.push.PostPushAction
import com.vztechcore.vzsync.domain.push.PushMode
import com.vztechcore.vzsync.domain.push.PushSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PushViewModel(application: Application) : AndroidViewModel(application) {

    private val app: Application = application
    private val strings: StringResolver = object : StringResolver {
        override fun get(id: Int, vararg args: Any): String = app.getString(id, *args)
    }

    private val commitStore = CommitHistoryStore.get(application)
    private val prefs = SecurePrefs.get(application)
    private val repoRepository = RepoRepository()

    private var pushSettings: PushSettings = PushSettings()
    private var username: String = ""

    private val _uiState = MutableStateFlow(PushUiState())
    val uiState: StateFlow<PushUiState> = _uiState.asStateFlow()

    fun reset(
        initialName: String,
        folderPath: String,
        username: String,
        settings: PushSettings,
    ) {
        this.pushSettings = settings
        this.username = username

        val initialMode = when (settings.defaultMode) {
            DefaultPushMode.SYNC -> PushMode.SYNC
            DefaultPushMode.REBUILD -> PushMode.REBUILD
        }

        _uiState.value = PushUiState(
            repoNameInput = initialName,
            folderPath = folderPath,
            logStyle = settings.logStyle,
            autoShowLog = settings.autoShowLog,
            postPushAction = settings.postPushAction,
            pushMode = initialMode,
            step = PushStep.SELECT_ACTION,
        )
    }

    fun selectCreate() {
        _uiState.update { it.copy(step = PushStep.CREATE_REPO, errorMessage = null) }
    }

    fun selectUpdate() {
        _uiState.update { it.copy(step = PushStep.SELECT_REPO, errorMessage = null) }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(repoNameInput = value) }
    }

    fun onVisibilityChange(isPrivate: Boolean) {
        _uiState.update { it.copy(isPrivate = isPrivate) }
    }

    fun selectRepo(repo: Repo) {
        _uiState.update {
            it.copy(selectedRepo = repo, step = PushStep.CONFIRM_REPO)
        }
    }

    fun setPushMode(mode: PushMode) {
        _uiState.update { it.copy(pushMode = mode) }
    }

    fun back() {
        _uiState.update { state ->
            when (state.step) {
                PushStep.CREATE_REPO -> state.copy(step = PushStep.SELECT_ACTION)
                PushStep.SELECT_REPO -> state.copy(step = PushStep.SELECT_ACTION)
                PushStep.CONFIRM_REPO -> state.copy(step = PushStep.SELECT_REPO, selectedRepo = null)
                PushStep.ERROR -> state.copy(
                    step = PushStep.SELECT_ACTION,
                    errorMessage = null,
                    errorReason = null,
                    errorTechnicalDetail = null,
                    logs = emptyList(),
                )
                else -> state
            }
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

    fun confirmAndPush() {
        val state = _uiState.value
        val isUpdate = state.selectedRepo != null
        val selectedRepo = state.selectedRepo
        val mode = state.pushMode
        val token = prefs.getToken().orEmpty()

        if (!isUpdate && !state.isSanitizedNameValid) return
        if (isUpdate && selectedRepo == null) return

        _uiState.update {
            it.copy(
                step = PushStep.PUSHING,
                logs = emptyList(),
                resultUrl = null,
                resultRepoName = null,
                resultCommitHash = null,
                resultFilesChanged = 0,
                errorMessage = null,
                errorReason = null,
                errorTechnicalDetail = null,
            )
        }

        viewModelScope.launch {
            val buffer = mutableListOf<LogEntry>()

            fun appendLog(level: LogLevel, message: String) {
                buffer += LogEntry(level, message)
                _uiState.update { it.copy(logs = buffer.toList()) }
            }

            fun fail(
                message: String,
                reason: FailureReason,
                technicalDetail: String? = null,
            ) {
                appendLog(LogLevel.ERROR, message)
                technicalDetail?.let {
                    appendLog(LogLevel.COMMENT, strings.get(R.string.git_log_details, it))
                }
                _uiState.update {
                    it.copy(
                        step = PushStep.ERROR,
                        errorMessage = message,
                        errorReason = reason,
                        errorTechnicalDetail = technicalDetail,
                    )
                }
            }

            if (token.isBlank()) {
                fail(
                    strings.get(R.string.push_vm_error_no_token),
                    FailureReason.NO_TOKEN,
                )
                return@launch
            }

            if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
                fail(
                    strings.get(R.string.push_vm_error_storage_permission),
                    FailureReason.STORAGE_PERMISSION_MISSING,
                )
                return@launch
            }

            val folderFile = resolveFolderFile(state.folderPath)
            if (folderFile == null) {
                fail(
                    strings.get(R.string.push_vm_error_folder_not_found),
                    FailureReason.FOLDER_NOT_FOUND,
                )
                return@launch
            }

            val targetUrl: String
            val repoFullName: String

            if (!isUpdate) {
                appendLog(LogLevel.COMMENT, strings.get(R.string.push_vm_log_creating_repo))
                val visibility = if (state.isPrivate) {
                    strings.get(R.string.push_vm_log_visibility_private)
                } else {
                    strings.get(R.string.push_vm_log_visibility_public)
                }
                appendLog(
                    LogLevel.COMMAND,
                    "POST /user/repos name=${state.sanitizedName} private=${state.isPrivate}",
                )

                when (val result = repoRepository.createRepo(
                    token = token,
                    name = state.sanitizedName,
                    isPrivate = state.isPrivate,
                )) {
                    is CreateRepoResult.Success -> {
                        targetUrl = result.repo.htmlUrl
                        repoFullName = result.repo.fullName
                        appendLog(
                            LogLevel.SUCCESS,
                            strings.get(
                                R.string.push_vm_log_repo_created,
                                result.repo.fullName,
                                visibility,
                            ),
                        )
                    }
                    is CreateRepoResult.Failure -> {
                        if (result.isAlreadyExists) {
                            targetUrl = "https://github.com/$username/${state.sanitizedName}"
                            repoFullName = "$username/${state.sanitizedName}"
                            appendLog(
                                LogLevel.COMMENT,
                                strings.get(R.string.push_vm_log_repo_exists),
                            )
                        } else {
                            fail(result.message, FailureReason.REPO_INIT_FAILED)
                            return@launch
                        }
                    }
                }
            } else {
                targetUrl = selectedRepo.htmlUrl
                repoFullName = selectedRepo.fullName
                appendLog(
                    LogLevel.COMMENT,
                    strings.get(R.string.push_vm_log_target_repo, repoFullName),
                )
            }

            val repoName = repoFullName.substringAfter('/')
            val commitMessage = resolveCommit(state, repoName)
            val logger = JGitLogger { level, message -> appendLog(level, message) }
            val pipeline = JGitPipeline(logger, strings)

            val result = pipeline.push(
                JGitPushRequest(
                    folder = folderFile,
                    repoUrl = targetUrl,
                    username = username,
                    token = token,
                    commitMessage = commitMessage,
                    mode = mode,
                    branch = "main",
                )
            )

            when (result) {
                is GitOperationResult.Success -> {
                    val commitMode = when {
                        result.wasInitial -> CommitMode.INITIAL
                        mode == PushMode.REBUILD -> CommitMode.REBUILD
                        else -> CommitMode.SYNC
                    }
                    commitStore.add(
                        CommitInfo(
                            hash = result.commitHash,
                            message = commitMessage,
                            repoFullName = repoFullName,
                            branch = "main",
                            timestamp = System.currentTimeMillis(),
                            filesChanged = result.filesChanged,
                            mode = commitMode,
                            folderDisplayPath = Formatters.shortFolderPath(state.folderPath),
                        )
                    )
                    _uiState.update {
                        it.copy(
                            step = PushStep.SUCCESS,
                            resultUrl = result.repoUrl,
                            resultRepoName = repoName,
                            resultCommitHash = result.commitHash,
                            resultFilesChanged = result.filesChanged,
                        )
                    }
                }
                is GitOperationResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            step = PushStep.ERROR,
                            errorMessage = result.message,
                            errorReason = result.reason,
                            errorTechnicalDetail = result.technicalDetail,
                        )
                    }
                }
            }
        }
    }

    fun dismiss() {
        _uiState.value = PushUiState()
        pushSettings = PushSettings()
        username = ""
    }

    private fun resolveFolderFile(path: String): File? {
        if (path.isBlank()) return null
        if (!path.startsWith("/")) return null
        val file = File(path)
        return if (file.exists() && file.isDirectory) file else null
    }

    private fun resolveCommit(state: PushUiState, repoName: String): String {
        val now = LocalDateTime.now()
        return pushSettings.commitMessageTemplate
            .replace("{date}", now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            .replace("{time}", now.format(DateTimeFormatter.ofPattern("HH:mm")))
            .replace("{folder}", state.folderPath)
            .replace("{repo}", repoName)
            .replace("{user}", username)
            .replace("{branch}", "main")
            .replace("{files}", "N")
            .replace("{mode}", state.pushMode.label)
    }
}