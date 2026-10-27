package com.vztechcore.vzsync.ui.screens.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import com.vztechcore.vzsync.data.prefs.AppSettingsStore
import com.vztechcore.vzsync.domain.ThemeMode
import com.vztechcore.vzsync.domain.language.AppLanguage
import com.vztechcore.vzsync.domain.push.PushSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val store = AppSettingsStore.get(application)

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            themeMode = store.getThemeMode(),
            language = store.getLanguage(),
            pushSettings = store.getPushSettings(),
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        store.setThemeMode(mode)
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setLanguage(language: AppLanguage) {
        store.setLanguage(language)
        _uiState.update { it.copy(language = language) }
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language.tag)
        )
    }

    fun setPushSettings(settings: PushSettings) {
        store.setPushSettings(settings)
        _uiState.update { it.copy(pushSettings = settings) }
    }
}