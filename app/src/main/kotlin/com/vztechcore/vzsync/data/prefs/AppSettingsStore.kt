package com.vztechcore.vzsync.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.vztechcore.vzsync.domain.ThemeMode
import com.vztechcore.vzsync.domain.language.AppLanguage
import com.vztechcore.vzsync.domain.push.DefaultPushMode
import com.vztechcore.vzsync.domain.push.LogStyle
import com.vztechcore.vzsync.domain.push.PostPushAction
import com.vztechcore.vzsync.domain.push.PushSettings

class AppSettingsStore private constructor(private val prefs: SharedPreferences) {

    // ----- Theme -----

    fun getThemeMode(): ThemeMode {
        val stored = prefs.getString(KEY_THEME, ThemeMode.AMOLED.name) ?: ThemeMode.AMOLED.name
        return runCatching { ThemeMode.valueOf(stored) }.getOrDefault(ThemeMode.AMOLED)
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
    }

    // ----- Language -----

    fun getLanguage(): AppLanguage {
        val stored = prefs.getString(KEY_LANGUAGE, null) ?: AppLanguage.PT_BR.tag
        return AppLanguage.fromTag(stored)
    }

    fun setLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.tag).apply()
    }

    // ----- Onboarding -----

    fun hasSeenOnboarding(): Boolean = prefs.getBoolean(KEY_SEEN_ONBOARDING, false)

    fun setSeenOnboarding() {
        prefs.edit().putBoolean(KEY_SEEN_ONBOARDING, true).apply()
    }

    // ----- Push settings -----

    fun getPushSettings(): PushSettings {
        return PushSettings(
            defaultMode = enumOrDefault(
                prefs.getString(KEY_PUSH_DEFAULT_MODE, null),
                DefaultPushMode.SYNC,
            ),
            postPushAction = enumOrDefault(
                prefs.getString(KEY_PUSH_ACTION, null),
                PostPushAction.COMMIT_AND_PUSH,
            ),
            commitMessageTemplate = prefs.getString(
                KEY_PUSH_COMMIT_MESSAGE,
                PushSettings.DEFAULT_MESSAGE,
            ) ?: PushSettings.DEFAULT_MESSAGE,
            logStyle = enumOrDefault(
                prefs.getString(KEY_PUSH_LOG_STYLE, null),
                LogStyle.TERMINAL,
            ),
            autoShowLog = prefs.getBoolean(KEY_PUSH_AUTO_SHOW_LOG, true),
        )
    }

    fun setPushSettings(settings: PushSettings) {
        prefs.edit()
            .putString(KEY_PUSH_DEFAULT_MODE, settings.defaultMode.name)
            .putString(KEY_PUSH_ACTION, settings.postPushAction.name)
            .putString(KEY_PUSH_COMMIT_MESSAGE, settings.commitMessageTemplate)
            .putString(KEY_PUSH_LOG_STYLE, settings.logStyle.name)
            .putBoolean(KEY_PUSH_AUTO_SHOW_LOG, settings.autoShowLog)
            .apply()
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(name: String?, default: T): T {
        if (name == null) return default
        return runCatching { enumValueOf<T>(name) }.getOrDefault(default)
    }

    companion object {
        private const val PREFS_NAME = "vz_app_settings"

        private const val KEY_THEME = "theme_mode"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_SEEN_ONBOARDING = "seen_onboarding"

        private const val KEY_PUSH_DEFAULT_MODE = "push_default_mode"
        private const val KEY_PUSH_ACTION = "push_action"
        private const val KEY_PUSH_COMMIT_MESSAGE = "push_commit_message"
        private const val KEY_PUSH_LOG_STYLE = "push_log_style"
        private const val KEY_PUSH_AUTO_SHOW_LOG = "push_auto_show_log"

        @Volatile
        private var instance: AppSettingsStore? = null

        fun get(context: Context): AppSettingsStore {
            return instance ?: synchronized(this) {
                instance ?: AppSettingsStore(
                    context.applicationContext
                        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                ).also { instance = it }
            }
        }
    }
}