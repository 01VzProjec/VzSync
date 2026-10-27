package com.vztechcore.vzsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vztechcore.vzsync.data.prefs.AppSettingsStore
import com.vztechcore.vzsync.ui.navigation.VzSyncNavHost
import com.vztechcore.vzsync.ui.screens.onboarding.OnboardingFlow
import com.vztechcore.vzsync.ui.screens.settings.SettingsViewModel
import com.vztechcore.vzsync.ui.theme.VzSyncTheme

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_OPEN_CLONE_SHEET = "open_clone_sheet"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = AppSettingsStore.get(this)
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(prefs.getLanguage().tag)
        )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            var hasSeenOnboarding by remember { mutableStateOf(prefs.hasSeenOnboarding()) }
            var pendingOpenClone by remember {
                mutableStateOf(
                    intent?.getBooleanExtra(EXTRA_OPEN_CLONE_SHEET, false) == true
                )
            }

            VzSyncTheme(themeMode = settingsState.themeMode) {
                if (!hasSeenOnboarding) {
                    OnboardingFlow(
                        onComplete = {
                            prefs.setSeenOnboarding()
                            hasSeenOnboarding = true
                        },
                        onLanguageSelected = { language ->
                            prefs.setLanguage(language)
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(language.tag)
                            )
                        },
                    )
                } else {
                    VzSyncNavHost(
                        settingsState = settingsState,
                        onThemeChange = settingsViewModel::setThemeMode,
                        onLanguageChange = settingsViewModel::setLanguage,
                        onPushSettingsChange = settingsViewModel::setPushSettings,
                        openCloneSheetOnLaunch = pendingOpenClone,
                        onCloneSheetLaunchConsumed = { pendingOpenClone = false },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}