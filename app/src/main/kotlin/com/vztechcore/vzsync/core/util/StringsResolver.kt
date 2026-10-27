package com.vztechcore.vzsync.core.util

import androidx.annotation.StringRes

/**
 * Resolves Android string resources outside of a Composable context.
 * Used by data-layer classes (e.g. JGitPipeline) that need localized
 * messages but cannot access a Context directly.
 */
interface StringResolver {
    fun get(@StringRes id: Int, vararg args: Any): String
}