package com.fitforge.app.data.model

enum class MealType(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACKS("Snacks")
}

data class FoodPreset(
    val id: String,
    val name: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val defaultMealType: MealType,
    val servingSize: String
)

data class LoggedMeal(
    val id: String,
    val foodName: String,
    val mealType: MealType,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class NutritionSummary(
    val dailyCalorieGoal: Int = 2400,
    val proteinGoalGrams: Int = 160,
    val carbsGoalGrams: Int = 250,
    val fatGoalGrams: Int = 70,
    val loggedMeals: List<LoggedMeal> = emptyList()
) {
    val totalCaloriesConsumed: Int
        get() = loggedMeals.sumOf { it.calories }

    val caloriesRemaining: Int
        get() = (dailyCalorieGoal - totalCaloriesConsumed).coerceAtLeast(0)

    val totalProtein: Int
        get() = loggedMeals.sumOf { it.proteinGrams }

    val totalCarbs: Int
        get() = loggedMeals.sumOf { it.carbsGrams }

    val totalFat: Int
        get() = loggedMeals.sumOf { it.fatGrams }
}
