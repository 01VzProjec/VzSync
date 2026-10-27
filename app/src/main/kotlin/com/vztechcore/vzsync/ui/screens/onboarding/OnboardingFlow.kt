package com.vztechcore.vzsync.ui.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.vztechcore.vzsync.domain.language.AppLanguage

@Composable
fun OnboardingFlow(
    onComplete: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
) {
    var step by rememberSaveable { mutableIntStateOf(0) }

    when (step) {
        0 -> LanguageSelectionScreen(
            onConfirm = { language ->
                onLanguageSelected(language)
                step = 1
            },
        )
        1 -> WelcomeScreen(
            onStart = onComplete,
        )
    }
}