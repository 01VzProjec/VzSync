package com.vztechcore.vzsync.data.auth

import com.vztechcore.vzsync.data.GitHubServiceProvider
import com.vztechcore.vzsync.domain.UserProfile
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AuthRepository {

    private val api = GitHubServiceProvider.api

    suspend fun validate(username: String, token: String): AuthResult {
        val trimmedUsername = username.trim()
        val trimmedToken = token.trim()

        if (trimmedUsername.isEmpty() || trimmedToken.isEmpty()) {
            return AuthResult.Failure(AuthFailureReason.EMPTY_FIELDS)
        }

        return try {
            val dto = api.getUser("Bearer $trimmedToken")

            if (!dto.login.equals(trimmedUsername, ignoreCase = true)) {
                return AuthResult.Failure(
                    AuthFailureReason.USER_MISMATCH,
                    "O token pertence à conta '${dto.login}'.",
                )
            }

            AuthResult.Success(
                UserProfile(
                    username = dto.login,
                    displayName = dto.name ?: dto.login,
                    avatarUrl = dto.avatarUrl,
                    bio = dto.bio,
                    publicRepos = dto.publicRepos,
                    followers = dto.followers,
                    following = dto.following,
                    createdAt = dto.createdAt,
                )
            )
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> AuthResult.Failure(AuthFailureReason.INVALID_TOKEN, "Token inválido ou expirado.")
                403 -> AuthResult.Failure(AuthFailureReason.INSUFFICIENT_SCOPE, "Token sem permissão. Verifique os escopos 'repo' e 'workflow'.")
                429 -> AuthResult.Failure(AuthFailureReason.RATE_LIMITED, "Limite de requisições atingido. Tente em alguns minutos.")
                else -> AuthResult.Failure(AuthFailureReason.UNKNOWN, "Erro ${e.code()}: ${e.message()}")
            }
        } catch (e: UnknownHostException) {
            AuthResult.Failure(AuthFailureReason.NETWORK_ERROR, "Sem conexão com a internet.")
        } catch (e: SocketTimeoutException) {
            AuthResult.Failure(AuthFailureReason.NETWORK_ERROR, "Tempo esgotado.")
        } catch (e: IOException) {
            AuthResult.Failure(AuthFailureReason.NETWORK_ERROR, "Falha de rede: ${e.message}")
        } catch (e: Exception) {
            AuthResult.Failure(AuthFailureReason.UNKNOWN, e.message)
        }
    }
}