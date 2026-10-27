package com.vztechcore.vzsync.domain.help

import androidx.annotation.StringRes

/**
 * Structured content block for a help section.
 * Each block is rendered with its own visual treatment.
 * Text is referenced by string resource so it can be localized.
 */
sealed interface HelpBlock {
    data class Heading(@StringRes val textRes: Int) : HelpBlock
    data class Paragraph(@StringRes val textRes: Int) : HelpBlock
    data class Bullet(@StringRes val textRes: Int) : HelpBlock
    data class Code(@StringRes val textRes: Int) : HelpBlock
    data class Tip(@StringRes val textRes: Int) : HelpBlock
    data class Warning(@StringRes val textRes: Int) : HelpBlock
    data class Danger(@StringRes val textRes: Int) : HelpBlock
}