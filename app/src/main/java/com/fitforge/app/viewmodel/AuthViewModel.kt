package com.fitforge.app.viewmodel

import androidx.lifecycle.ViewModel
import com.fitforge.app.firebase.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val authManager = FirebaseAuthManager()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        _authState.value = AuthState.Loading
        authManager.registerUser(
            email = email,
            password = password,
            onSuccess = {
                _authState.value = AuthState.Success("Registration successful!")
                onSuccess()
            },
            onFailure = { rawError ->
                val errorMsg = mapAuthError(rawError)
                _authState.value = AuthState.Error(errorMsg)
                onFailure(errorMsg)
            }
        )
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        _authState.value = AuthState.Loading
        authManager.loginUser(
            email = email,
            password = password,
            onSuccess = {
                _authState.value = AuthState.Success("Login successful!")
                onSuccess()
            },
            onFailure = { rawError ->
                val errorMsg = mapAuthError(rawError)
                _authState.value = AuthState.Error(errorMsg)
                onFailure(errorMsg)
            }
        )
    }

    fun sendPasswordReset(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        _authState.value = AuthState.Loading
        authManager.sendPasswordResetEmail(
            email = email,
            onSuccess = {
                _authState.value = AuthState.Success("Password reset email sent!")
                onSuccess()
            },
            onFailure = { rawError ->
                val errorMsg = mapAuthError(rawError)
                _authState.value = AuthState.Error(errorMsg)
                onFailure(errorMsg)
            }
        )
    }

    fun logout() {
        authManager.logout()
        _authState.value = AuthState.Idle
    }

    fun isLoggedIn(): Boolean {
        return authManager.isUserLoggedIn()
    }

    fun currentUserEmail(): String? {
        return authManager.getCurrentUserEmail()
    }

    private fun mapAuthError(error: String): String {
        return when {
            error.contains("badly formatted", ignoreCase = true) -> "Invalid email address format."
            error.contains("already in use", ignoreCase = true) -> "An account with this email already exists."
            error.contains("invalid credential", ignoreCase = true) || 
            error.contains("wrong password", ignoreCase = true) ||
            error.contains("user-not-found", ignoreCase = true) ||
            error.contains("user not found", ignoreCase = true) -> "Incorrect email or password."
            error.contains("weak password", ignoreCase = true) -> "Password should be at least 6 characters."
            error.contains("network error", ignoreCase = true) -> "Network error. Please check your internet connection."
            else -> error
        }
    }
}