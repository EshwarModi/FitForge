package com.fitforge.app.data.model

enum class FitnessGoal(val displayName: String) {
    WEIGHT_LOSS("Weight Loss"),
    MUSCLE_GAIN("Muscle Gain"),
    GENERAL_FITNESS("General Fitness"),
    STRENGTH("Strength"),
    ENDURANCE("Endurance")
}

enum class ActivityLevel(val displayName: String) {
    SEDENTARY("Sedentary (Little to no exercise)"),
    LIGHTLY_ACTIVE("Lightly Active (1-3 days/week)"),
    MODERATELY_ACTIVE("Moderately Active (3-5 days/week)"),
    VERY_ACTIVE("Very Active (6-7 days/week)")
}

enum class UnitsSystem(val displayName: String) {
    METRIC("Metric (kg, cm, L)"),
    IMPERIAL("Imperial (lbs, ft, oz)")
}

data class UserProfile(
    val name: String = "Athlete",
    val email: String = "user@fitforge.com",
    val age: Int = 24,
    val gender: String = "Male",
    val heightCm: Double = 170.0,
    val weightKg: Double = 62.0,
    val fitnessGoal: FitnessGoal = FitnessGoal.GENERAL_FITNESS,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val unitsSystem: UnitsSystem = UnitsSystem.METRIC
)
