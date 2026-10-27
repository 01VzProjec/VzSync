package com.vztechcore.vzsync.domain.help

/**
 * Identifies the exact place in the Help screen that should be opened.
 * When [anchor] is non-null in a future version, the target block inside
 * the section will be highlighted. Today only the section itself is scrolled to.
 */
data class HelpTarget(
    val section: HelpSection,
    val anchor: String? = null,
)