package com.vztechcore.vzsync.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vztechcore.vzsync.domain.ThemeMode
import com.vztechcore.vzsync.domain.help.HelpSection
import com.vztechcore.vzsync.domain.help.HelpTarget
import com.vztechcore.vzsync.domain.push.PushSettings
import com.vztechcore.vzsync.domain.language.AppLanguage
import com.vztechcore.vzsync.ui.components.FileInfoBar
import com.vztechcore.vzsync.ui.components.FileInfoSheet
import com.vztechcore.vzsync.ui.screens.about.AboutScreen
import com.vztechcore.vzsync.ui.screens.advanced.AdvancedActionsScreen
import com.vztechcore.vzsync.ui.screens.advanced.AdvancedActionsViewModel
import com.vztechcore.vzsync.ui.screens.auth.AuthBottomSheet
import com.vztechcore.vzsync.ui.screens.auth.AuthViewModel
import com.vztechcore.vzsync.ui.screens.clone.CloneSheet
import com.vztechcore.vzsync.ui.screens.clone.CloneStep
import com.vztechcore.vzsync.ui.screens.clone.CloneViewModel
import com.vztechcore.vzsync.ui.screens.help.HelpScreen
import com.vztechcore.vzsync.ui.screens.help.HelpViewModel
import com.vztechcore.vzsync.ui.screens.home.HomeScreen
import com.vztechcore.vzsync.ui.screens.home.HomeViewModel
import com.vztechcore.vzsync.ui.screens.licenses.LicensesScreen
import com.vztechcore.vzsync.ui.screens.push.PushSheet
import com.vztechcore.vzsync.ui.screens.push.PushStep
import com.vztechcore.vzsync.ui.screens.push.PushViewModel
import com.vztechcore.vzsync.ui.screens.repos.ReposScreen
import com.vztechcore.vzsync.ui.screens.repos.ReposViewModel
import com.vztechcore.vzsync.ui.screens.settings.SettingsScreen
import com.vztechcore.vzsync.ui.screens.settings.SettingsUiState
import com.vztechcore.vzsync.ui.screens.settings.push.PushSettingsScreen
import com.vztechcore.vzsync.ui.theme.DarkPrimary
import com.vztechcore.vzsync.ui.theme.DarkSecondary
import com.vztechcore.vzsync.ui.theme.VzBackground
import com.vztechcore.vzsync.ui.theme.VzOnSurface
import com.vztechcore.vzsync.ui.theme.VzOnSurfaceVariant
import com.vztechcore.vzsync.ui.theme.VzSurface
import com.vztechcore.vzsync.ui.theme.VzSurfaceVariant
import java.io.File

@Composable
fun VzSyncNavHost(
    settingsState: SettingsUiState,
    onThemeChange: (ThemeMode) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onPushSettingsChange: (PushSettings) -> Unit,
    openCloneSheetOnLaunch: Boolean = false,
    onCloneSheetLaunchConsumed: () -> Unit = {},
) {
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val reposViewModel: ReposViewModel = viewModel()
    val pushViewModel: PushViewModel = viewModel()
    val helpViewModel: HelpViewModel = viewModel()
    val advancedViewModel: AdvancedActionsViewModel = viewModel()
    val cloneViewModel: CloneViewModel = viewModel()

    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val reposState by reposViewModel.uiState.collectAsStateWithLifecycle()
    val pushState by pushViewModel.uiState.collectAsStateWithLifecycle()
    val helpState by helpViewModel.uiState.collectAsStateWithLifecycle()
    val advancedState by advancedViewModel.uiState.collectAsStateWithLifecycle()
    val cloneState by cloneViewModel.uiState.collectAsStateWithLifecycle()

    var showAuthSheet by remember { mutableStateOf(false) }
    var showRepos by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showPushSettings by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showLicenses by remember { mutableStateOf(false) }
    var showPushSheet by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showAdvancedActions by remember { mutableStateOf(false) }

    var reopenPushSheetAfterHelp by remember { mutableStateOf(false) }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= 33) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasNotificationPermission = granted
    }

    fun ensureNotificationPermission(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= 33 && !hasNotificationPermission) {
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            onGranted()
        } else {
            onGranted()
        }
    }

    BackHandler(
        enabled = showAdvancedActions || showHelp || showAbout || showLicenses ||
            showPushSettings || showSettings || showRepos
    ) {
        when {
            showAdvancedActions -> showAdvancedActions = false
            showHelp -> {
                showHelp = false
                helpViewModel.reset()
                if (reopenPushSheetAfterHelp) {
                    reopenPushSheetAfterHelp = false
                    showPushSheet = true
                }
            }
            showAbout -> showAbout = false
            showLicenses -> showLicenses = false
            showPushSettings -> showPushSettings = false
            showSettings -> showSettings = false
            showRepos -> showRepos = false
        }
    }

    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) showAuthSheet = false else showRepos = false
    }

    LaunchedEffect(showRepos) {
        if (showRepos && reposState.repos.isEmpty() && !reposState.isLoading) {
            reposViewModel.loadRepos(authState.token, authState.currentProfile)
        }
    }

    LaunchedEffect(pushState.step) {
        if (pushState.step == PushStep.SELECT_REPO &&
            reposState.repos.isEmpty() &&
            !reposState.isLoading
        ) {
            reposViewModel.loadRepos(authState.token, authState.currentProfile)
        }
    }

    LaunchedEffect(openCloneSheetOnLaunch) {
        if (openCloneSheetOnLaunch) {
            homeViewModel.openCloneSheet()
            cloneViewModel.showSheet()
            onCloneSheetLaunchConsumed()
        }
    }

    val selectedFile = homeState.selectedFile
    val showFileSheet = homeState.showFileInfoSheet

    val openHelp: (HelpTarget) -> Unit = { target ->
        helpViewModel.reset()
        helpViewModel.focusOn(target.section)
        showHelp = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VzBackground),
    ) {
        when {
            showAdvancedActions -> AdvancedActionsScreen(
                uiState = advancedState,
                onBack = { showAdvancedActions = false },
                onActionClick = advancedViewModel::onActionClick,
                onSelectRepo = advancedViewModel::selectRepo,
                onConfirmPending = advancedViewModel::confirmPending,
                onCancelPending = advancedViewModel::cancelPending,
                onDismissLastCommitSheet = advancedViewModel::dismissLastCommitSheet,
                onDismissBranchSheet = advancedViewModel::dismissBranchSheet,
                onChangeBranch = advancedViewModel::changeBranch,
                onFeedbackConsumed = advancedViewModel::consumeFeedback,
                onHelpRequested = {
                    openHelp(HelpTarget(HelpSection.ADVANCED_ACTIONS))
                },
            )
            showHelp -> HelpScreen(
                uiState = helpState,
                onQueryChange = helpViewModel::onQueryChange,
                onToggleSection = helpViewModel::toggleSection,
                onAutoScrollConsumed = helpViewModel::consumeAutoScroll,
                onBack = {
                    showHelp = false
                    helpViewModel.reset()
                    if (reopenPushSheetAfterHelp) {
                        reopenPushSheetAfterHelp = false
                        showPushSheet = true
                    }
                },
            )
            showAbout -> AboutScreen(onBack = { showAbout = false })
            showLicenses -> LicensesScreen(onBack = { showLicenses = false })
            showPushSettings -> PushSettingsScreen(
                settings = settingsState.pushSettings,
                onBack = { showPushSettings = false },
                onChange = onPushSettingsChange,
                onHelpRequested = {
                    openHelp(HelpTarget(HelpSection.PUSH_SETTINGS))
                },
            )
            showSettings -> SettingsScreen(
                state = settingsState,
                onBack = { showSettings = false },
                onThemeChange = onThemeChange,
                onLanguageChange = onLanguageChange,
                onAboutClick = { showAbout = true },
                onLicensesClick = { showLicenses = true },
                onPushSettingsClick = { showPushSettings = true },
                onAdvancedActionsClick = {
                    advancedViewModel.refresh(
                        folderHint = homeState.selectedFolderName
                            ?.takeIf { it.isNotBlank() }
                    )
                    showAdvancedActions = true
                },
                onHelpRequested = openHelp,
            )
            showRepos -> ReposScreen(
                state = reposState,
                onBack = { showRepos = false },
                onRefresh = { reposViewModel.refresh() },
                onFilterChange = reposViewModel::setFilter,
                onRepoClick = reposViewModel::onRepoClick,
                onDismissRepoSheet = reposViewModel::dismissRepoSheet,
                onRequestDelete = reposViewModel::requestDelete,
                onCancelDelete = reposViewModel::cancelDelete,
                onConfirmDelete = reposViewModel::confirmDelete,
                onFeedbackConsumed = reposViewModel::consumeFeedback,
                onHelpRequested = openHelp,
            )
            else -> HomeScreen(
                profile = authState.currentProfile,
                state = homeState,
                onAvatarClick = { showAuthSheet = true },
                onSettingsClick = { showSettings = true },
                onFolderPicked = homeViewModel::onFolderSelected,
                onPushClick = {
                    if (!authState.isLoggedIn) {
                        showAuthSheet = true
                    } else {
                        pushViewModel.reset(
                            initialName = homeState.selectedFolderName.orEmpty(),
                            folderPath = homeState.selectedFolderPath
                                ?: homeState.selectedFolderName.orEmpty(),
                            username = authState.currentProfile?.username.orEmpty(),
                            settings = settingsState.pushSettings,
                        )
                        showPushSheet = true
                    }
                },
                onCloneClick = {
                    if (!authState.isLoggedIn) {
                        showAuthSheet = true
                    } else {
                        ensureNotificationPermission {
                            cloneViewModel.reset()
                            homeViewModel.openCloneSheet()
                        }
                    }
                },
                onRefreshTree = homeViewModel::refreshTree,
                onClearFolder = homeViewModel::clearFolder,
                onToggleExpand = homeViewModel::toggleExpand,
                onFileClick = homeViewModel::onFileClick,
                onDrawerHelpRequested = {
                    openHelp(HelpTarget(HelpSection.HOME_DRAWER))
                },
            )
        }

        AnimatedVisibility(
            visible = !showSettings && !showAbout && !showLicenses && !showPushSettings &&
                !showHelp && !showAdvancedActions &&
                selectedFile != null && !showFileSheet,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(220),
            ) + fadeIn(tween(220)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(180),
            ) + fadeOut(tween(180)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            FileInfoBar(
                fileName = selectedFile?.name.orEmpty(),
                onMore = homeViewModel::showFileInfoSheet,
                onDismiss = homeViewModel::dismissFileBar,
            )
        }

        if (homeState.showCloneSheet && cloneState.isHidden &&
            cloneState.step == CloneStep.CLONING
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(
                        color = VzSurfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = DarkSecondary,
                    strokeWidth = 2.dp,
                )
                Text(
                    text = "Clonando ${cloneState.percentage}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = VzOnSurface,
                )
                TextButton(onClick = cloneViewModel::showSheet) {
                    Text(
                        text = "Ver",
                        color = DarkSecondary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }

    if (showAuthSheet) {
        AuthBottomSheet(
            state = authState,
            onUsernameChange = authViewModel::onUsernameChange,
            onTokenChange = authViewModel::onTokenChange,
            onToggleVisibility = authViewModel::toggleTokenVisibility,
            onSubmit = authViewModel::submit,
            onLogout = {
                authViewModel.logout()
                reposViewModel.clear()
                showAuthSheet = false
            },
            onViewRepos = {
                showAuthSheet = false
                showRepos = true
            },
            onDismiss = { showAuthSheet = false },
        )
    }

    if (showFileSheet && selectedFile != null) {
        FileInfoSheet(
            file = selectedFile,
            onOpenInOtherApp = homeViewModel::openSelectedFileInOtherApp,
            onClose = homeViewModel::hideFileInfoSheet,
        )
    }

    if (showPushSheet) {
        PushSheet(
            state = pushState,
            repos = reposState.repos,
            isLoadingRepos = reposState.isLoading,
            reposError = reposState.error,
            onDismiss = {
                pushViewModel.dismiss()
                showPushSheet = false
            },
            onCreateSelected = pushViewModel::selectCreate,
            onUpdateSelected = pushViewModel::selectUpdate,
            onNameChange = pushViewModel::onNameChange,
            onVisibilityChange = pushViewModel::onVisibilityChange,
            onRepoSelected = pushViewModel::selectRepo,
            onBack = pushViewModel::back,
            onConfirm = { pushViewModel.confirmAndPush() },
            onRetryLoadRepos = {
                reposViewModel.loadRepos(authState.token, authState.currentProfile)
            },
            onModeChange = pushViewModel::setPushMode,
            onHelpRequested = {
                reopenPushSheetAfterHelp = pushState.step != PushStep.SELECT_ACTION
                showPushSheet = false
                openHelp(HelpTarget(HelpSection.PUSH_OVERVIEW))
            },
            onOpenStorageSettings = pushViewModel::openStorageSettings,
        )
    }

    if (homeState.showCloneSheet && !cloneState.isHidden) {
        CloneSheet(
            state = cloneState,
            onDismiss = {
                if (cloneState.step == CloneStep.SUCCESS) {
                    cloneState.resultPath?.let { path ->
                        homeViewModel.onClonedFolderReady(File(path))
                    }
                }
                cloneViewModel.dismiss()
                homeViewModel.dismissCloneSheet()
            },
            onUrlChange = cloneViewModel::onUrlChange,
            onConfirm = {
                cloneViewModel.startClone(
                    username = authState.currentProfile?.username.orEmpty(),
                    token = authState.token,
                ) { _, _ -> }
            },
            onRetry = {
                cloneViewModel.startClone(
                    username = authState.currentProfile?.username.orEmpty(),
                    token = authState.token,
                ) { _, _ -> }
            },
            onHide = cloneViewModel::hideSheet,
            onCancel = cloneViewModel::cancelClone,
            onOpenStorageSettings = cloneViewModel::openStorageSettings,
            onHelpRequested = {
                homeViewModel.dismissCloneSheet()
                openHelp(HelpTarget(HelpSection.CLONE_REPO))
            },
        )
    }

    if (cloneState.showXiaomiDialog) {
        AlertDialog(
            onDismissRequest = cloneViewModel::dismissXiaomiDialog,
            containerColor = VzSurface,
            titleContentColor = VzOnSurface,
            textContentColor = VzOnSurfaceVariant,
            title = {
                Text(
                    text = "Otimização de bateria (Xiaomi)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "No Xiaomi, o sistema pode fechar o VzSync em segundo plano " +
                        "durante a clonagem. Para evitar isso, permita que o VzSync rode " +
                        "sem restrições de bateria nas configurações.",
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        cloneViewModel.openXiaomiBatterySettings()
                        cloneViewModel.dismissXiaomiDialog()
                    },
                ) {
                    Text(
                        text = "Abrir configurações",
                        color = DarkPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = cloneViewModel::dismissXiaomiDialog) {
                    Text(
                        text = "Agora não",
                        color = VzOnSurfaceVariant,
                    )
                }
            },
        )
    }
}