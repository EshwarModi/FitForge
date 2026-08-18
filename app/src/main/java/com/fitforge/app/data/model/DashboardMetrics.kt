package com.fitforge.app.data.model

data class DashboardMetrics(
    val userName: String = "Athlete",
    val todayWorkoutCompleted: Int = 0,
    val todayWorkoutTotal: Int = 1,
    val todayWorkoutName: String = "Full Body Blitz",
    val caloriesBurned: Int = 420,
    val caloriesTarget: Int = 600,
    val stepsCount: Int = 6240,
    val stepsTarget: Int = 10000,
    val waterIntakeLiters: Double = 1.8,
    val waterTargetLiters: Double = 3.0,
    val currentWeightKg: Double = 62.0,
    val heightCm: Double = 170.0
) {
    val bmi: Double
        get() {
            val heightMeters = heightCm / 100.0
            return if (heightMeters > 0) currentWeightKg / (heightMeters * heightMeters) else 0.0
        }

    val bmiCategory: String
        get() = when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal weight"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obese"
        }
}
