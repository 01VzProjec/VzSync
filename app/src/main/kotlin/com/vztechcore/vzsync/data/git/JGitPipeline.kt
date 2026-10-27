package com.vztechcore.vzsync.data.git

import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.core.util.StringResolver
import com.vztechcore.vzsync.domain.LogLevel
import com.vztechcore.vzsync.domain.push.PushMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.URIish
import java.io.File

class JGitPipeline(
    private val logger: JGitLogger,
    private val strings: StringResolver,
) {

    suspend fun push(request: JGitPushRequest): GitOperationResult = withContext(Dispatchers.IO) {
        val folder = request.folder

        if (!folder.exists()) {
            return@withContext fail(
                strings.get(R.string.git_fail_folder_not_found, folder.absolutePath),
                FailureReason.FOLDER_NOT_FOUND,
            )
        }
        if (!folder.isDirectory) {
            return@withContext fail(
                strings.get(R.string.git_fail_not_a_directory, folder.absolutePath),
                FailureReason.NOT_A_DIRECTORY,
            )
        }
        if (!folder.canRead()) {
            return@withContext fail(
                strings.get(R.string.git_fail_storage_permission),
                FailureReason.STORAGE_PERMISSION_MISSING,
            )
        }

        val gitDir = File(folder, ".git")
        var git: Git? = null

        try {
            val isNew = !gitDir.exists()

            // 1. Init or open
            if (isNew) {
                logger.log(LogLevel.COMMENT, strings.get(R.string.git_log_init_repo))
                logger.log(LogLevel.COMMAND, "git init -b ${request.branch}")
                git = Git.init()
                    .setDirectory(folder)
                    .setInitialBranch(request.branch)
                    .call()
                logger.log(
                    LogLevel.SUCCESS,
                    strings.get(R.string.git_log_repo_initialized, request.branch),
                )
            } else {
                logger.log(LogLevel.COMMENT, strings.get(R.string.git_log_opening_repo))
                logger.log(LogLevel.COMMAND, "git status")
                try {
                    git = Git.open(folder)
                } catch (e: Exception) {
                    return@withContext fail(
                        strings.get(R.string.git_fail_repo_open),
                        FailureReason.REPO_OPEN_FAILED,
                        technicalDetail = describe(e),
                    )
                }
                logger.log(LogLevel.SUCCESS, strings.get(R.string.git_log_repo_opened))
            }

            // 2. Configure identity
            val config = git.repository.config
            if (config.getString("user", null, "name") == null) {
                logger.log(LogLevel.COMMENT, strings.get(R.string.git_log_configuring_identity))
                config.setString("user", null, "name", request.username)
                config.setString(
                    "user",
                    null,
                    "email",
                    "${request.username}@users.noreply.github.com",
                )
                config.save()
                logger.log(
                    LogLevel.SUCCESS,
                    strings.get(R.string.git_log_identity_set, request.username),
                )
            }

            // 3. Remote
            val remoteUrl = if (request.repoUrl.endsWith(".git")) request.repoUrl
                            else "${request.repoUrl}.git"
            val existingRemote = config.getString("remote", "origin", "url")

            if (existingRemote != remoteUrl) {
                logger.log(LogLevel.COMMENT, strings.get(R.string.git_log_configuring_remote))
                logger.log(LogLevel.COMMAND, "git remote add origin $remoteUrl")
                if (existingRemote != null) {
                    config.setString("remote", "origin", "url", remoteUrl)
                    config.save()
                    logger.log(LogLevel.SUCCESS, strings.get(R.string.git_log_remote_updated))
                } else {
                    git.remoteAdd()
                        .setName("origin")
                        .setUri(URIish(remoteUrl))
                        .call()
                    logger.log(LogLevel.SUCCESS, strings.get(R.string.git_log_remote_added))
                }
            } else {
                logger.log(LogLevel.COMMENT, strings.get(R.string.git_log_remote_configured))
            }

            // 4. Rebuild: untrack everything (only if repo has a HEAD)
            val hasHead = try {
                git.repository.resolve("HEAD") != null
            } catch (_: Exception) {
                false
            }

            if (request.mode == PushMode.REBUILD && hasHead) {
                logger.log(LogLevel.COMMENT, strings.get(R.string.git_log_rebuild_untracking))
                logger.log(LogLevel.COMMAND, "git rm -rf --cached .")
                try {
                    git.rm()
                        .addFilepattern(".")
                        .setCached(true)
                        .call()
                    logger.log(LogLevel.SUCCESS, strings.get(R.string.git_log_all_untracked))
                } catch (e: Exception) {
                    logger.log(LogLevel.COMMENT, strings.get(R.string.git_log_nothing_untracked))
                }
            }

            // 5. Add all
            logger.log(LogLevel.COMMAND, "git add .")
            try {
                git.add().addFilepattern(".").call()
            } catch (e: Exception) {
                return@withContext fail(
                    strings.get(R.string.git_fail_add, e.message.orEmpty()),
                    FailureReason.ADD_FAILED,
                    technicalDetail = describe(e),
                )
            }
            logger.log(LogLevel.SUCCESS, strings.get(R.string.git_log_files_staged))

            // 6. Check changes
            val status = try {
                git.status().call()
            } catch (_: Exception) {
                null
            }
            val changes = status?.let {
                it.added.size + it.changed.size + it.removed.size
            } ?: 0

            if (changes == 0 && !isNew && request.mode == PushMode.SYNC) {
                logger.log(LogLevel.SUCCESS, strings.get(R.string.git_log_no_changes))
                val headHash = try {
                    git.repository.resolve("HEAD")?.name?.take(7) ?: "—"
                } catch (_: Exception) {
                    "—"
                }
                return@withContext GitOperationResult.Success(
                    commitHash = headHash,
                    filesChanged = 0,
                    repoUrl = request.repoUrl,
                )
            }

            // 7. Commit
            logger.log(LogLevel.COMMAND, "git commit -m \"${request.commitMessage}\"")
            val commit = try {
                git.commit()
                    .setMessage(request.commitMessage)
                    .call()
            } catch (e: GitAPIException) {
                val msg = e.message ?: strings.get(R.string.common_unknown_error)
                return@withContext fail(
                    strings.get(R.string.git_fail_commit, msg),
                    FailureReason.COMMIT_FAILED,
                    technicalDetail = describe(e),
                )
            }
            val shortHash = commit.name.take(7)
            logger.log(
                LogLevel.SUCCESS,
                strings.get(R.string.git_log_commit_created, shortHash),
            )

            // 8. Ensure local branch name matches requested branch.
            // Repositories initialized by older app versions may still be on
            // "master". If the current branch differs from the requested one,
            // rename it so the ref we push actually exists.
            val currentBranch = try {
                git.repository.branch
            } catch (_: Exception) {
                null
            }
            if (currentBranch != null && currentBranch != request.branch) {
                logger.log(
                    LogLevel.COMMENT,
                    strings.get(R.string.git_log_renaming_branch, currentBranch, request.branch),
                )
                try {
                    git.branchRename()
                        .setOldName(currentBranch)
                        .setNewName(request.branch)
                        .call()
                    logger.log(
                        LogLevel.SUCCESS,
                        strings.get(R.string.git_log_renaming_branch, currentBranch, request.branch),
                    )
                } catch (e: Exception) {
                    logger.log(
                        LogLevel.COMMENT,
                        "renaming failed: ${describe(e)}",
                    )
                }
            }

            // 9. Push — use whatever branch is active now (should equal request.branch)
            val pushBranch = try {
                git.repository.branch ?: request.branch
            } catch (_: Exception) {
                request.branch
            }
            val forcePush = request.mode == PushMode.REBUILD
            logger.log(
                LogLevel.COMMAND,
                if (forcePush) "git push --force origin $pushBranch"
                else "git push -u origin $pushBranch",
            )

            try {
                git.push()
                    .setRemote("origin")
                    .setCredentialsProvider(gitHubCredentials(request.username, request.token))
                    .setForce(forcePush)
                    .add("refs/heads/$pushBranch:refs/heads/$pushBranch")
                    .call()
                logger.log(LogLevel.SUCCESS, strings.get(R.string.git_log_push_completed))
            } catch (e: Exception) {
                // Always surface the raw exception first so the user (and the
                // app's own log panel) can see exactly what JGit reported.
                val technical = describe(e)
                logger.log(
                    LogLevel.ERROR,
                    strings.get(R.string.git_log_push_failed, technical),
                )
                e.cause?.let { cause ->
                    logger.log(
                        LogLevel.COMMENT,
                        strings.get(R.string.git_log_caused_by, describe(cause)),
                    )
                }

                val (reason, friendly) = classifyPushError(e)
                return@withContext fail(friendly, reason, technicalDetail = technical)
            }

            return@withContext GitOperationResult.Success(
                commitHash = shortHash,
                filesChanged = changes,
                repoUrl = request.repoUrl,
                wasInitial = isNew,
            )
        } catch (e: GitAPIException) {
            fail(
                strings.get(R.string.git_fail_git_error),
                FailureReason.UNKNOWN,
                technicalDetail = describe(e),
            )
        } catch (e: Exception) {
            fail(
                strings.get(R.string.git_fail_unexpected),
                FailureReason.UNKNOWN,
                technicalDetail = describe(e),
            )
        } finally {
            try {
                git?.close()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Classifies a push exception into a UI-friendly reason plus a short
     * actionable message. Uses both the exception type name and the message
     * text because JGit does not always throw specific subtypes consistently.
     */
    private fun classifyPushError(e: Exception): Pair<FailureReason, String> {
        val typeName = e.javaClass.simpleName
        val raw = e.message.orEmpty()
        val causeType = e.cause?.javaClass?.simpleName.orEmpty()

        val auth = typeName.contains("NotAuthorized", true) ||
            typeName.contains("InvalidCredentials", true) ||
            causeType.contains("NotAuthorized", true) ||
            raw.contains("not authorized", true) ||
            raw.contains("Authentication failed", true) ||
            raw.contains("Invalid username or password", true) ||
            raw.contains("401")

        if (auth) {
            return FailureReason.PUSH_AUTH_FAILED to
                strings.get(R.string.git_push_auth_failed)
        }

        val network = typeName.contains("Transport", true) ||
            typeName.contains("SSL", true) ||
            causeType.contains("UnknownHost", true) ||
            causeType.contains("SocketTimeout", true) ||
            causeType.contains("ConnectException", true) ||
            raw.contains("UnknownHost", true) ||
            raw.contains("timeout", true) ||
            raw.contains("Connection refused", true) ||
            raw.contains("Unable to connect", true)

        if (network) {
            return FailureReason.PUSH_NETWORK_FAILED to
                strings.get(R.string.git_push_network_failed)
        }

        val rejected = raw.contains("rejected", true) ||
            raw.contains("non-fast-forward", true) ||
            raw.contains("protected branch", true) ||
            raw.contains("GH006")

        if (rejected) {
            return FailureReason.PUSH_REJECTED to
                strings.get(R.string.git_push_rejected)
        }

        val notFound = raw.contains("not found", true) || raw.contains("404")
        if (notFound) {
            return FailureReason.REMOTE_FAILED to
                strings.get(R.string.git_push_not_found)
        }

        val fallbackName = typeName.ifBlank { strings.get(R.string.common_unknown_error) }
        return FailureReason.PUSH_FAILED to
            strings.get(R.string.git_push_generic_failed, fallbackName)
    }

    /**
     * Builds a compact, log-friendly description of an exception.
     * Example: "TransportException: https://github.com/...: not authorized"
     */
    private fun describe(e: Throwable): String {
        val type = e.javaClass.simpleName.ifBlank { "Exception" }
        val msg = e.message?.trim().orEmpty()
        return if (msg.isBlank()) type else "$type: $msg"
    }

    private fun fail(
        message: String,
        reason: FailureReason,
        technicalDetail: String? = null,
    ): GitOperationResult.Failure {
        logger.log(LogLevel.ERROR, message)
        if (technicalDetail != null) {
            logger.log(
                LogLevel.COMMENT,
                strings.get(R.string.git_log_details, technicalDetail),
            )
        }
        return GitOperationResult.Failure(message, reason, technicalDetail)
    }
}