package com.vztechcore.vzsync.data.git

import com.vztechcore.vzsync.domain.LogLevel
import com.vztechcore.vzsync.domain.clone.GitHubUrlParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.errors.RepositoryNotFoundException
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class JGitCloner(private val logger: JGitLogger) {

    suspend fun clone(
        url: String,
        username: String,
        token: String,
        baseDir: File,
        cancelRequested: AtomicBoolean,
        onProgress: (Int, String, Boolean) -> Unit,
    ): CloneResult = withContext(Dispatchers.IO) {

        val parsed = GitHubUrlParser.parse(url)
            ?: return@withContext fail(
                "URL inválida. Cole o endereço HTTPS do repositório " +
                    "(ex: https://github.com/usuario/repo).",
                CloneFailureReason.INVALID_URL,
            )

        if (token.isBlank()) {
            return@withContext fail(
                "Token do GitHub não encontrado. Reconecte a conta.",
                CloneFailureReason.AUTH_FAILED,
            )
        }

        try {
            if (!baseDir.exists() && !baseDir.mkdirs()) {
                return@withContext fail(
                    "Não foi possível criar a pasta ${baseDir.absolutePath}. " +
                        "Verifique o acesso a arquivos nas configurações.",
                    CloneFailureReason.BASE_DIR_UNAVAILABLE,
                )
            }
            if (!baseDir.isDirectory || !baseDir.canWrite()) {
                return@withContext fail(
                    "A pasta ${baseDir.absolutePath} não pode ser escrita. " +
                        "Verifique o acesso a arquivos nas configurações.",
                    CloneFailureReason.BASE_DIR_UNAVAILABLE,
                )
            }
        } catch (e: Exception) {
            return@withContext fail(
                "Falha ao preparar a pasta de destino: ${e.message}",
                CloneFailureReason.BASE_DIR_UNAVAILABLE,
            )
        }

        val destination = findAvailableDestination(baseDir, parsed.repo)

        logger.log(LogLevel.COMMENT, "Cloning ${parsed.owner}/${parsed.repo}")
        logger.log(LogLevel.COMMAND, "git clone ${parsed.canonicalUrl} ${destination.name}")

        var git: Git? = null
        try {
            val monitor = PercentageProgressMonitor(
                onProgress = onProgress,
                cancelRequested = cancelRequested,
            )

            git = Git.cloneRepository()
                .setURI(parsed.canonicalUrl)
                .setDirectory(destination)
                .setCloneAllBranches(true)
                .setTimeout(180)
                .setProgressMonitor(monitor)
                .setCredentialsProvider(gitHubCredentials(username, token))
                .call()

            if (cancelRequested.get()) {
                cleanupDestination(destination)
                return@withContext fail(
                    "Clonagem cancelada pelo usuário.",
                    CloneFailureReason.CANCELLED,
                )
            }

            logger.log(LogLevel.SUCCESS, "Repository cloned to ${destination.absolutePath}")

            val headHash = try {
                git.repository.resolve("HEAD")?.name?.take(7) ?: "—"
            } catch (_: Exception) {
                "—"
            }
            logger.log(LogLevel.SUCCESS, "Checked out at $headHash")

            return@withContext CloneResult.Success(
                file = destination,
                repoName = destination.name,
                repoFullName = "${parsed.owner}/${parsed.repo}",
            )
        } catch (e: org.eclipse.jgit.api.errors.CanceledException) {
            cleanupDestination(destination)
            fail("Clonagem cancelada pelo usuário.", CloneFailureReason.CANCELLED)
        } catch (e: InvalidRemoteException) {
            cleanupDestination(destination)
            fail(
                "URL remota inválida. Verifique se o repositório existe.",
                CloneFailureReason.REPO_NOT_FOUND,
            )
        } catch (e: TransportException) {
            cleanupDestination(destination)
            val raw = e.message ?: ""
            val friendly = when {
                raw.contains("not authorized", ignoreCase = true) ||
                    raw.contains("Authentication failed", ignoreCase = true) ->
                    "Autenticação falhou. Verifique se o token tem o escopo 'repo'."
                raw.contains("not found", ignoreCase = true) ->
                    "Repositório não encontrado. Pode ser privado e exigir um token válido."
                raw.contains("unable to access", ignoreCase = true) ||
                    raw.contains("timeout", ignoreCase = true) ->
                    "Falha de rede ao clonar. Verifique sua conexão."
                else -> "Falha ao clonar: $raw"
            }
            val reason = when {
                raw.contains("not authorized", ignoreCase = true) ||
                    raw.contains("Authentication failed", ignoreCase = true) ->
                    CloneFailureReason.AUTH_FAILED
                raw.contains("not found", ignoreCase = true) ->
                    CloneFailureReason.REPO_NOT_FOUND
                else -> CloneFailureReason.NETWORK_ERROR
            }
            fail(friendly, reason)
        } catch (e: GitAPIException) {
            cleanupDestination(destination)
            if (cancelRequested.get()) {
                fail("Clonagem cancelada.", CloneFailureReason.CANCELLED)
            } else {
                fail(e.message ?: "Erro no Git ao clonar.", CloneFailureReason.CLONE_FAILED)
            }
        } catch (e: RepositoryNotFoundException) {
            cleanupDestination(destination)
            fail("Repositório não encontrado.", CloneFailureReason.REPO_NOT_FOUND)
        } catch (e: Exception) {
            cleanupDestination(destination)
            if (cancelRequested.get()) {
                fail("Clonagem cancelada.", CloneFailureReason.CANCELLED)
            } else {
                fail(e.message ?: "Erro inesperado ao clonar.", CloneFailureReason.UNKNOWN)
            }
        } finally {
            try {
                git?.close()
            } catch (_: Exception) {
            }
        }
    }

    private fun findAvailableDestination(baseDir: File, repoName: String): File {
        val candidate = File(baseDir, repoName)
        if (!candidate.exists()) return candidate
        if (candidate.listFiles().isNullOrEmpty()) return candidate
        for (counter in 2..100) {
            val alt = File(baseDir, "$repoName-$counter")
            if (!alt.exists() || alt.listFiles().isNullOrEmpty()) return alt
        }
        return candidate
    }

    private fun cleanupDestination(destination: File) {
        try {
            destination.deleteRecursively()
        } catch (_: Exception) {
        }
    }

    private fun fail(message: String, reason: CloneFailureReason): CloneResult.Failure {
        logger.log(LogLevel.ERROR, message)
        return CloneResult.Failure(message, reason)
    }
}