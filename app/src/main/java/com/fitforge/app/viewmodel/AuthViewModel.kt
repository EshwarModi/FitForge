package com.fitforge.app.viewmodel

import androidx.lifecycle.ViewModel
import com.fitforge.app.firebase.FirebaseAuthManager

class AuthViewModel : ViewModel() {

    private val authManager = FirebaseAuthManager()

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        authManager.registerUser(
            email = email,
            password = password,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        authManager.loginUser(
            email = email,
            password = password,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun logout() {
        authManager.logout()
    }

    fun isLoggedIn(): Boolean {
        return authManager.isUserLoggedIn()
    }

    fun currentUserEmail(): String? {
        return authManager.getCurrentUserEmail()
    }
}