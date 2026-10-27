package com.vztechcore.vzsync.domain.push 

/**
 * Converts user input into a valid GitHub repository name.
 *
 * Rules (based on GitHub):
 *  - lowercase only
 *  - spaces become hyphens
 *  - only letters, digits, dot, underscore, hyphen are kept
 *  - consecutive hyphens are collapsed
 *  - cannot start/end with hyphen, dot, or underscore
 *  - max 100 characters
 */
object RepoNameSanitizer {

    private const val MAX_LENGTH = 100
    private val MULTIPLE_WHITESPACE = Regex("\\s+")
    private val INVALID_CHARS = Regex("[^a-z0-9._-]")
    private val MULTIPLE_HYPHENS = Regex("-+")

    fun sanitize(input: String): String = input
        .trim()
        .lowercase()
        .replace(MULTIPLE_WHITESPACE, "-")
        .replace(INVALID_CHARS, "-")
        .replace(MULTIPLE_HYPHENS, "-")
        .trim('-', '.', '_')
        .take(MAX_LENGTH)

    fun isValid(sanitized: String): Boolean =
        sanitized.isNotBlank() && sanitized.length <= MAX_LENGTH
}