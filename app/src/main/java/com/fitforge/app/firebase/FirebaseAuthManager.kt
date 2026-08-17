package com.fitforge.app.firebase

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Register a new user
    fun registerUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Registration Failed")
            }
    }

    // Login existing user
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Login Failed")
            }
    }

    // Logout current user
    fun logout() {
        auth.signOut()
    }

    // Check current logged-in user
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Get current user's email
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    // Send password reset email
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Failed to send reset email")
            }
    }
}