package com.fitforge.app.viewmodel

import androidx.lifecycle.ViewModel
import com.fitforge.app.data.model.ActivityLevel
import com.fitforge.app.data.model.FitnessGoal
import com.fitforge.app.data.model.UnitsSystem
import com.fitforge.app.data.model.UserProfile
import com.fitforge.app.firebase.FirebaseAuthManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val authManager = FirebaseAuthManager()

    private val _profile = MutableStateFlow(
        UserProfile(
            name = authManager.getCurrentUserEmail()?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Athlete",
            email = authManager.getCurrentUserEmail() ?: "athlete@fitforge.com"
        )
    )
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    fun updateProfile(
        name: String,
        age: Int,
        gender: String,
        heightCm: Double,
        weightKg: Double,
        goal: FitnessGoal,
        activityLevel: ActivityLevel
    ) {
        val current = _profile.value
        _profile.value = current.copy(
            name = name,
            age = age,
            gender = gender,
            heightCm = heightCm,
            weightKg = weightKg,
            fitnessGoal = goal,
            activityLevel = activityLevel
        )
    }

    fun toggleNotifications(enabled: Boolean) {
        _profile.value = _profile.value.copy(notificationsEnabled = enabled)
    }

    fun toggleDarkMode(enabled: Boolean) {
        _profile.value = _profile.value.copy(darkModeEnabled = enabled)
    }

    fun updateUnits(units: UnitsSystem) {
        _profile.value = _profile.value.copy(unitsSystem = units)
    }

    fun logout() {
        authManager.logout()
    }

    fun deleteAccount(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUser.delete()
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { ex ->
                    onFailure(ex.message ?: "Failed to delete account. You may need to re-authenticate first.")
                }
        } else {
            onFailure("No user is currently logged in.")
        }
    }
}
