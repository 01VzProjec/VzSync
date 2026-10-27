package com.vztechcore.vzsync.domain.clone

/**
 * Parses GitHub HTTPS URLs into structured pieces.
 * Accepts:
 *   https://github.com/user/repo
 *   https://github.com/user/repo.git
 *   https://github.com/user/repo/
 *   http://github.com/user/repo
 * Rejects:
 *   SSH URLs (git@github.com:user/repo.git)
 *   Other hosts (gitlab, bitbucket)
 *   Malformed URLs
 */
object GitHubUrlParser {

    private val HTTPS_REGEX = Regex(
        """^https?://(?:www\.)?github\.com/([A-Za-z0-9._-]+)/([A-Za-z0-9._-]+?)(?:\.git)?/?$""",
        RegexOption.IGNORE_CASE,
    )

    data class Parsed(
        val owner: String,
        val repo: String,
        val canonicalUrl: String,
    )

    fun parse(input: String): Parsed? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null
        val match = HTTPS_REGEX.matchEntire(trimmed) ?: return null
        val owner = match.groupValues[1]
        val repo = match.groupValues[2]
        if (owner.isBlank() || repo.isBlank()) return null
        return Parsed(
            owner = owner,
            repo = repo,
            canonicalUrl = "https://github.com/$owner/$repo.git",
        )
    }
}