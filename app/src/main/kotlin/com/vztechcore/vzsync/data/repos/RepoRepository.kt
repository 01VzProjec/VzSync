package com.vztechcore.vzsync.data.repos

import com.vztechcore.vzsync.data.GitHubServiceProvider
import com.vztechcore.vzsync.domain.Repo
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed interface ReposResult {
    data class Success(val repos: List<Repo>) : ReposResult
    data class Failure(val message: String) : ReposResult
}

sealed interface FileCountResult {
    data class Success(val count: Int) : FileCountResult
    data class Failure(val message: String) : FileCountResult
}

sealed interface DeleteResult {
    data object Success : DeleteResult
    data class Failure(val message: String) : DeleteResult
}

sealed interface CreateRepoResult {
    data class Success(val repo: Repo) : CreateRepoResult
    data class Failure(
        val message: String,
        val isAlreadyExists: Boolean = false,
    ) : CreateRepoResult
}

class RepoRepository {

    private val api = GitHubServiceProvider.api

    suspend fun fetchAllRepos(token: String): ReposResult {
        return try {
            val all = mutableListOf<GitHubRepoDto>()
            var page = 1
            var hasMore = true

            while (hasMore && page <= 10) {
                val batch = api.getUserRepos(
                    authHeader = "Bearer $token",
                    page = page,
                    perPage = 100,
                )
                all += batch
                hasMore = batch.size == 100
                page++
            }

            ReposResult.Success(all.map { it.toDomain() })
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> ReposResult.Failure("Token inválido ou expirado.")
                403 -> ReposResult.Failure("Sem permissão para listar repositórios.")
                429 -> ReposResult.Failure("Limite de requisições atingido.")
                else -> ReposResult.Failure("Erro ${e.code()}: ${e.message()}")
            }
        } catch (_: UnknownHostException) {
            ReposResult.Failure("Sem conexão com a internet.")
        } catch (_: SocketTimeoutException) {
            ReposResult.Failure("Tempo esgotado ao buscar repositórios.")
        } catch (e: IOException) {
            ReposResult.Failure("Falha de rede: ${e.message}")
        } catch (e: Exception) {
            ReposResult.Failure(e.message ?: "Erro inesperado.")
        }
    }

    suspend fun createRepo(
        token: String,
        name: String,
        isPrivate: Boolean,
    ): CreateRepoResult {
        return try {
            val dto = api.createRepo(
                authHeader = "Bearer $token",
                body = CreateRepoRequest(
                    name = name,
                    isPrivate = isPrivate,
                    autoInit = false,
                ),
            )
            CreateRepoResult.Success(dto.toDomain())
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> CreateRepoResult.Failure("Token inválido ou expirado.")
                403 -> CreateRepoResult.Failure(
                    "Sem permissão para criar repositórios. Verifique o escopo 'repo'."
                )
                422 -> CreateRepoResult.Failure(
                    "Já existe um repositório com esse nome.",
                    isAlreadyExists = true,
                )
                429 -> CreateRepoResult.Failure("Limite de requisições atingido.")
                else -> CreateRepoResult.Failure("Erro ${e.code()}: ${e.message()}")
            }
        } catch (_: UnknownHostException) {
            CreateRepoResult.Failure("Sem conexão com a internet.")
        } catch (_: SocketTimeoutException) {
            CreateRepoResult.Failure("Tempo esgotado ao criar repositório.")
        } catch (e: IOException) {
            CreateRepoResult.Failure("Falha de rede: ${e.message}")
        } catch (e: Exception) {
            CreateRepoResult.Failure(e.message ?: "Erro inesperado.")
        }
    }

    suspend fun fetchFileCount(token: String, repo: Repo): FileCountResult {
        return try {
            val owner = repo.fullName.substringBefore('/')
            val tree = api.getRepoTree(
                authHeader = "Bearer $token",
                owner = owner,
                repo = repo.name,
                branch = repo.defaultBranch,
            )
            if (tree.truncated) {
                return FileCountResult.Failure("Repositório muito grande para contar todos os arquivos.")
            }
            val count = tree.tree.count { it.type == "blob" }
            FileCountResult.Success(count)
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> FileCountResult.Failure("Branch '${repo.defaultBranch}' não encontrado.")
                409 -> FileCountResult.Failure("Repositório vazio.")
                else -> FileCountResult.Failure("Erro ${e.code()} ao contar arquivos.")
            }
        } catch (_: IOException) {
            FileCountResult.Failure("Falha de rede ao contar arquivos.")
        } catch (e: Exception) {
            FileCountResult.Failure(e.message ?: "Erro ao contar arquivos.")
        }
    }

    suspend fun deleteRepo(token: String, repo: Repo): DeleteResult {
        return try {
            val owner = repo.fullName.substringBefore('/')
            api.deleteRepo(
                authHeader = "Bearer $token",
                owner = owner,
                repo = repo.name,
            )
            DeleteResult.Success
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> DeleteResult.Failure("Token inválido ou expirado.")
                403 -> DeleteResult.Failure(
                    "Sem permissão. Seu token precisa do escopo 'delete_repo'. " +
                        "Gere um novo token no GitHub e reconecte."
                )
                404 -> DeleteResult.Failure("Repositório não encontrado.")
                else -> DeleteResult.Failure("Erro ${e.code()}: ${e.message()}")
            }
        } catch (_: UnknownHostException) {
            DeleteResult.Failure("Sem conexão com a internet.")
        } catch (_: SocketTimeoutException) {
            DeleteResult.Failure("Tempo esgotado ao excluir repositório.")
        } catch (e: IOException) {
            DeleteResult.Failure("Falha de rede: ${e.message}")
        } catch (e: Exception) {
            DeleteResult.Failure(e.message ?: "Erro inesperado.")
        }
    }

    private fun GitHubRepoDto.toDomain() = Repo(
        name = name,
        fullName = fullName,
        description = description,
        isPrivate = isPrivate,
        htmlUrl = htmlUrl,
        language = language,
        stars = stars,
        forks = forks,
        watchers = watchers,
        updatedAtIso = updatedAtIso,
        sizeKb = size,
        defaultBranch = defaultBranch,
        openIssues = openIssues,
    )
}