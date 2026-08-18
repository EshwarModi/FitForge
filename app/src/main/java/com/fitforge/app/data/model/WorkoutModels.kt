package com.fitforge.app.data.model

enum class MuscleCategory(val displayName: String) {
    ALL("All"),
    CHEST("Chest"),
    BACK("Back"),
    LEGS("Legs"),
    SHOULDERS("Shoulders"),
    ARMS("Arms"),
    CORE("Core"),
    FULL_BODY("Full Body"),
    CARDIO("Cardio")
}

enum class DifficultyLevel(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

data class Exercise(
    val id: String,
    val name: String,
    val category: MuscleCategory,
    val targetMuscle: String,
    val difficulty: DifficultyLevel,
    val equipment: String,
    val instructions: List<String>,
    val defaultSets: Int = 3,
    val defaultReps: Int = 12,
    val restDurationSeconds: Int = 60
)

data class WorkoutPlan(
    val id: String,
    val title: String,
    val description: String,
    val category: MuscleCategory,
    val difficulty: DifficultyLevel,
    val estimatedMinutes: Int,
    val exercises: List<Exercise>
)

data class WorkoutHistoryItem(
    val id: String,
    val planTitle: String,
    val category: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val completedAt: Long = System.currentTimeMillis()
)
