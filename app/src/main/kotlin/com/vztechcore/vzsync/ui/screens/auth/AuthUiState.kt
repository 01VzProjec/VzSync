package com.vztechcore.vzsync.ui.screens.auth

import com.vztechcore.vzsync.domain.UserProfile

data class AuthUiState(
    val username: String = "",
    val token: String = "",
    val isTokenVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isRestoring: Boolean = false,
    val errorMessage: String? = null,
    val currentProfile: UserProfile? = null,
    val isLoggedIn: Boolean = false,
)