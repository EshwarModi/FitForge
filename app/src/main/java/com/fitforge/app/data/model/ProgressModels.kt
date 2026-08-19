package com.fitforge.app.data.model

enum class ProgressTimeframe(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

data class WaterLogEntry(
    val id: String,
    val amountLiters: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class WeightLogEntry(
    val id: String,
    val weightKg: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class BodyMetrics(
    val heightCm: Double = 170.0,
    val currentWeightKg: Double = 62.0,
    val targetWeightKg: Double = 60.0,
    val weightLogs: List<WeightLogEntry> = emptyList()
) {
    val heightMeters: Double
        get() = heightCm / 100.0

    val bmi: Double
        get() = if (heightMeters > 0) currentWeightKg / (heightMeters * heightMeters) else 0.0

    val bmiCategory: String
        get() = when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal weight"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obese"
        }

    val bmiDescription: String
        get() = when (bmiCategory) {
            "Underweight" -> "Consider a nutrient-dense diet to build muscle and achieve healthy weight gain."
            "Normal weight" -> "Great job! Maintain your balanced diet and regular workout routine."
            "Overweight" -> "Focus on consistent cardio workouts and a slight caloric deficit."
            else -> "Consult with a health professional for a tailored nutrition and exercise plan."
        }

    val minHealthyWeightKg: Double
        get() = 18.5 * (heightMeters * heightMeters)

    val maxHealthyWeightKg: Double
        get() = 24.9 * (heightMeters * heightMeters)
}
