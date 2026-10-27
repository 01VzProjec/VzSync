package com.vztechcore.vzsync.data.git

import com.vztechcore.vzsync.domain.LogLevel
import com.vztechcore.vzsync.domain.push.PushMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.transport.URIish
import java.io.File

class JGitPipeline(private val logger: JGitLogger) {

    suspend fun push(request: JGitPushRequest): GitOperationResult = withContext(Dispatchers.IO) {
        val folder = request.folder

        if (!folder.exists()) {
            return@withContext fail(
                "A pasta não existe: ${folder.absolutePath}",
                FailureReason.FOLDER_NOT_FOUND,
            )
        }
        if (!folder.isDirectory) {
            return@withContext fail(
                "O caminho não é uma pasta: ${folder.absolutePath}",
                FailureReason.NOT_A_DIRECTORY,
            )
        }
        if (!folder.canRead()) {
            return@withContext fail(
                "Sem permissão para acessar a pasta. Conceda \"Acesso a todos os arquivos\" " +
                    "nas configurações do Android.",
                FailureReason.STORAGE_PERMISSION_MISSING,
            )
        }

        val gitDir = File(folder, ".git")
        var git: Git? = null

        try {
            val isNew = !gitDir.exists()

            // 1. Init or open
            if (isNew) {
                logger.log(LogLevel.COMMENT, "Initializing local repository")
                logger.log(LogLevel.COMMAND, "git init -b ${request.branch}")
                git = Git.init()
                    .setDirectory(folder)
                    .setInitialBranch(request.branch)
                    .call()
                logger.log(LogLevel.SUCCESS, "Repository initialized on branch ${request.branch}")
            } else {
                logger.log(LogLevel.COMMENT, "Opening existing repository")
                logger.log(LogLevel.COMMAND, "git status")
                try {
                    git = Git.open(folder)
                } catch (e: Exception) {
                    return@withContext fail(
                        "Repositório local inválido. Apague a pasta .git e tente novamente.",
                        FailureReason.REPO_OPEN_FAILED,
                    )
                }
                logger.log(LogLevel.SUCCESS, "Repository opened")
            }

            // 2. Configure identity
            val config = git.repository.config
            if (config.getString("user", null, "name") == null) {
                logger.log(LogLevel.COMMENT, "Configuring commit identity")
                config.setString("user", null, "name", request.username)
                config.setString(
                    "user",
                    null,
                    "email",
                    "${request.username}@users.noreply.github.com",
                )
                config.save()
                logger.log(LogLevel.SUCCESS, "Identity set to ${request.username}")
            }

            // 3. Remote
            val remoteUrl = if (request.repoUrl.endsWith(".git")) request.repoUrl
                            else "${request.repoUrl}.git"
            val existingRemote = config.getString("remote", "origin", "url")

            if (existingRemote != remoteUrl) {
                logger.log(LogLevel.COMMENT, "Configuring remote 'origin'")
                logger.log(LogLevel.COMMAND, "git remote add origin $remoteUrl")
                if (existingRemote != null) {
                    config.setString("remote", "origin", "url", remoteUrl)
                    config.save()
                    logger.log(LogLevel.SUCCESS, "Remote updated")
                } else {
                    git.remoteAdd()
                        .setName("origin")
                        .setUri(URIish(remoteUrl))
                        .call()
                    logger.log(LogLevel.SUCCESS, "Remote added")
                }
            } else {
                logger.log(LogLevel.COMMENT, "Remote 'origin' already configured")
            }

            // 4. Rebuild: untrack everything (only if repo has a HEAD)
            val hasHead = try {
                git.repository.resolve("HEAD") != null
            } catch (_: Exception) {
                false
            }

            if (request.mode == PushMode.REBUILD && hasHead) {
                logger.log(LogLevel.COMMENT, "Rebuild mode: untracking all files")
                logger.log(LogLevel.COMMAND, "git rm -rf --cached .")
                try {
                    git.rm()
                        .addFilepattern(".")
                        .setCached(true)
                        .call()
                    logger.log(LogLevel.SUCCESS, "All files untracked")
                } catch (e: Exception) {
                    logger.log(LogLevel.COMMENT, "Nothing to untrack")
                }
            }

            // 5. Add all
            logger.log(LogLevel.COMMAND, "git add .")
            try {
                git.add().addFilepattern(".").call()
            } catch (e: Exception) {
                return@withContext fail(
                    "Falha ao adicionar arquivos: ${e.message}",
                    FailureReason.ADD_FAILED,
                )
            }
            logger.log(LogLevel.SUCCESS, "Files staged")

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
                logger.log(
                    LogLevel.SUCCESS,
                    "No changes to commit — everything is already in sync",
                )
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
                return@withContext fail(
                    "Falha no commit: ${e.message ?: "erro desconhecido"}",
                    FailureReason.COMMIT_FAILED,
                )
            }
            val shortHash = commit.name.take(7)
            logger.log(LogLevel.SUCCESS, "Commit created: $shortHash")

            // 8. Push
            val forcePush = request.mode == PushMode.REBUILD
            logger.log(
                LogLevel.COMMAND,
                if (forcePush) "git push --force origin ${request.branch}"
                else "git push -u origin ${request.branch}",
            )

            try {
                git.push()
                    .setRemote("origin")
                    .setCredentialsProvider(gitHubCredentials(request.username, request.token))
                    .setForce(forcePush)
                    .add("refs/heads/${request.branch}:refs/heads/${request.branch}")
                    .call()
            } catch (e: TransportException) {
                val raw = e.message ?: "Falha no transporte"
                val friendly = when {
                    raw.contains("not authorized", ignoreCase = true) ||
                        raw.contains("Authentication failed", ignoreCase = true) ->
                        "Autenticação falhou. Verifique se o token tem o escopo 'repo'."
                    raw.contains("not found", ignoreCase = true) ->
                        "Repositório remoto não encontrado. Verifique o nome."
                    raw.contains("rejected", ignoreCase = true) ||
                        raw.contains("non-fast-forward", ignoreCase = true) ->
                        "Push rejeitado. O repositório remoto tem mudanças que você não tem. " +
                            "Use o modo Reconstruir para sobrescrever."
                    else -> raw
                }
                return@withContext fail(friendly, FailureReason.PUSH_FAILED)
            } catch (e: GitAPIException) {
                return@withContext fail(
                    e.message ?: "Falha no push",
                    FailureReason.PUSH_FAILED,
                )
            }
            logger.log(LogLevel.SUCCESS, "Push completed")

            return@withContext GitOperationResult.Success(
                commitHash = shortHash,
                filesChanged = changes,
                repoUrl = request.repoUrl,
                wasInitial = isNew,
            )
        } catch (e: GitAPIException) {
            fail(e.message ?: "Erro no Git", FailureReason.UNKNOWN)
        } catch (e: Exception) {
            fail(e.message ?: "Erro inesperado", FailureReason.UNKNOWN)
        } finally {
            try {
                git?.close()
            } catch (_: Exception) {
            }
        }
    }

    private fun fail(message: String, reason: FailureReason): GitOperationResult.Failure {
        logger.log(LogLevel.ERROR, message)
        return GitOperationResult.Failure(message, reason)
    }
}