package com.vztechcore.vzsync.ui.screens.settings

import com.vztechcore.vzsync.domain.ThemeMode
import com.vztechcore.vzsync.domain.language.AppLanguage
import com.vztechcore.vzsync.domain.push.PushSettings

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.AMOLED,
    val language: AppLanguage = AppLanguage.PT_BR,
    val pushSettings: PushSettings = PushSettings(),
)