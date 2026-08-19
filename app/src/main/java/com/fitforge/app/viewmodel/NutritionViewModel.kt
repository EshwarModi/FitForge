package com.fitforge.app.viewmodel

import androidx.lifecycle.ViewModel
import com.fitforge.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NutritionViewModel : ViewModel() {

    // Common healthy fitness food presets
    val presetFoods = listOf(
        FoodPreset("fp_1", "Oatmeal with Berries", 320, 12, 54, 6, MealType.BREAKFAST, "1 Bowl (250g)"),
        FoodPreset("fp_2", "Boiled Eggs (2 whole)", 140, 12, 1, 10, MealType.BREAKFAST, "2 Large Eggs"),
        FoodPreset("fp_3", "Avocado Toast", 280, 8, 30, 14, MealType.BREAKFAST, "1 Slice Whole Grain"),
        FoodPreset("fp_4", "Grilled Chicken Breast", 350, 48, 0, 8, MealType.LUNCH, "200g Chicken"),
        FoodPreset("fp_5", "Brown Rice & Veggies", 260, 6, 52, 3, MealType.LUNCH, "1 Cup (180g)"),
        FoodPreset("fp_6", "Whey Protein Shake", 180, 30, 4, 3, MealType.SNACKS, "1 Scoop with Water"),
        FoodPreset("fp_7", "Greek Yogurt with Honey", 210, 18, 22, 4, MealType.SNACKS, "200g Container"),
        FoodPreset("fp_8", "Baked Salmon Filet", 420, 38, 0, 24, MealType.DINNER, "220g Salmon"),
        FoodPreset("fp_9", "Steamed Broccoli & Sweet Potato", 220, 5, 48, 1, MealType.DINNER, "1 Medium Potato + Broccoli"),
        FoodPreset("fp_10", "Almonds & Apple", 220, 6, 26, 12, MealType.SNACKS, "Handful (30g) + 1 Apple")
    )

    private val initialLoggedMeals = listOf(
        LoggedMeal("lm_1", "Oatmeal with Berries", MealType.BREAKFAST, 320, 12, 54, 6),
        LoggedMeal("lm_2", "Boiled Eggs (2 whole)", MealType.BREAKFAST, 140, 12, 1, 10),
        LoggedMeal("lm_3", "Grilled Chicken Breast", MealType.LUNCH, 350, 48, 0, 8),
        LoggedMeal("lm_4", "Brown Rice & Veggies", MealType.LUNCH, 260, 6, 52, 3),
        LoggedMeal("lm_5", "Whey Protein Shake", MealType.SNACKS, 180, 30, 4, 3),
        LoggedMeal("lm_6", "Baked Salmon Filet", MealType.DINNER, 420, 38, 0, 24)
    )

    private val _summary = MutableStateFlow(
        NutritionSummary(
            dailyCalorieGoal = 2400,
            proteinGoalGrams = 160,
            carbsGoalGrams = 250,
            fatGoalGrams = 70,
            loggedMeals = initialLoggedMeals
        )
    )
    val summary: StateFlow<NutritionSummary> = _summary.asStateFlow()

    fun logPresetFood(preset: FoodPreset, targetMealType: MealType) {
        val newMeal = LoggedMeal(
            id = "lm_${System.currentTimeMillis()}",
            foodName = preset.name,
            mealType = targetMealType,
            calories = preset.calories,
            proteinGrams = preset.proteinGrams,
            carbsGrams = preset.carbsGrams,
            fatGrams = preset.fatGrams
        )
        val current = _summary.value
        _summary.value = current.copy(loggedMeals = current.loggedMeals + newMeal)
    }

    fun logCustomFood(
        name: String,
        mealType: MealType,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int
    ) {
        val newMeal = LoggedMeal(
            id = "lm_${System.currentTimeMillis()}",
            foodName = name,
            mealType = mealType,
            calories = calories,
            proteinGrams = protein,
            carbsGrams = carbs,
            fatGrams = fat
        )
        val current = _summary.value
        _summary.value = current.copy(loggedMeals = current.loggedMeals + newMeal)
    }

    fun deleteLoggedMeal(mealId: String) {
        val current = _summary.value
        _summary.value = current.copy(loggedMeals = current.loggedMeals.filterNot { it.id == mealId })
    }

    fun updateCalorieGoal(newGoal: Int) {
        val current = _summary.value
        _summary.value = current.copy(dailyCalorieGoal = newGoal)
    }
}
