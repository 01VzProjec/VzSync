package com.vztechcore.vzsync.domain.language

enum class AppLanguage(
    val tag: String,
    val displayNameRes: Int,
) {
    PT_BR("pt-BR", com.vztechcore.vzsync.R.string.language_pt_br),
    EN("en", com.vztechcore.vzsync.R.string.language_en),
    ;

    companion object {
        fun fromTag(tag: String): AppLanguage =
            entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } ?: PT_BR
    }
}