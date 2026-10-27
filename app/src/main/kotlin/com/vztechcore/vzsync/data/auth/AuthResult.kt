package com.vztechcore.vzsync.data.auth

import com.vztechcore.vzsync.domain.UserProfile

sealed interface AuthResult {
    data class Success(val profile: UserProfile) : AuthResult
    data class Failure(
        val reason: AuthFailureReason,
        val technicalMessage: String? = null,
    ) : AuthResult
}

enum class AuthFailureReason {
    EMPTY_FIELDS,
    INVALID_TOKEN,
    INSUFFICIENT_SCOPE,
    USER_MISMATCH,
    NETWORK_ERROR,
    RATE_LIMITED,
    UNKNOWN,
}