package com.vztechcore.vzsync.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vztechcore.vzsync.R
import com.vztechcore.vzsync.data.auth.AuthFailureReason
import com.vztechcore.vzsync.data.auth.AuthRepository
import com.vztechcore.vzsync.data.auth.AuthResult
import com.vztechcore.vzsync.data.prefs.SecurePrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = SecurePrefs.get(application)
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        restoreSession()
    }

    private fun restoreSession() {
        val token = prefs.getToken().orEmpty()
        val username = prefs.getUsername().orEmpty()
        if (token.isBlank() || username.isBlank()) return

        val cached = prefs.getCachedProfile()
        if (cached != null) {
            _uiState.update {
                it.copy(
                    username = username,
                    token = token,
                    isLoggedIn = true,
                    isRestoring = true,
                    currentProfile = cached,
                )
            }
        }

        viewModelScope.launch {
            when (val result = repository.validate(username, token)) {
                is AuthResult.Success -> {
                    prefs.saveProfile(result.profile)
                    _uiState.update {
                        it.copy(
                            isLoggedIn = true,
                            isRestoring = false,
                            currentProfile = result.profile,
                            errorMessage = null,
                        )
                    }
                }
                is AuthResult.Failure -> {
                    prefs.clearAuth()
                    _uiState.update {
                        it.copy(
                            isLoggedIn = false,
                            isRestoring = false,
                            currentProfile = null,
                            token = "",
                            username = "",
                        )
                    }
                }
            }
        }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onTokenChange(value: String) {
        _uiState.update { it.copy(token = value, errorMessage = null) }
    }

    fun toggleTokenVisibility() {
        _uiState.update { it.copy(isTokenVisible = !it.isTokenVisible) }
    }

    fun submit() {
        val state = _uiState.value
        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = repository.validate(state.username, state.token)) {
                is AuthResult.Success -> {
                    prefs.saveAuth(result.profile.username, state.token.trim())
                    prefs.saveProfile(result.profile)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            currentProfile = result.profile,
                            errorMessage = null,
                        )
                    }
                }
                is AuthResult.Failure -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.technicalMessage
                            ?: fallbackMessage(result.reason),
                    )
                }
            }
        }
    }

    fun logout() {
        prefs.clearAuth()
        _uiState.update {
            it.copy(
                currentProfile = null,
                isLoggedIn = false,
                isRestoring = false,
                token = "",
                username = "",
                errorMessage = null,
            )
        }
    }

    private fun fallbackMessage(reason: AuthFailureReason): String {
        val resId = when (reason) {
            AuthFailureReason.EMPTY_FIELDS -> R.string.auth_err_empty
            AuthFailureReason.INVALID_TOKEN -> R.string.auth_err_invalid_token
            AuthFailureReason.INSUFFICIENT_SCOPE -> R.string.auth_err_scope
            AuthFailureReason.USER_MISMATCH -> R.string.auth_err_user_mismatch
            AuthFailureReason.NETWORK_ERROR -> R.string.auth_err_network
            AuthFailureReason.RATE_LIMITED -> R.string.auth_err_rate_limited
            AuthFailureReason.UNKNOWN -> R.string.auth_err_unknown
        }
        return getApplication<Application>().getString(resId)
    }
}