package com.vztechcore.vzsync.data.git

import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

/**
 * GitHub HTTPS credentials for JGit.
 * Use the GitHub username as the user and the PAT as the password.
 */
internal fun gitHubCredentials(username: String, token: String): CredentialsProvider =
    UsernamePasswordCredentialsProvider(username, token)