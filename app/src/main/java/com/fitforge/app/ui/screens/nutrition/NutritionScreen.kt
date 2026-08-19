package com.fitforge.app.ui.screens.nutrition

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitforge.app.data.model.LoggedMeal
import com.fitforge.app.data.model.MealType
import com.fitforge.app.viewmodel.NutritionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    nutritionViewModel: NutritionViewModel = viewModel()
) {
    val context = LocalContext.current
    val summary by nutritionViewModel.summary.collectAsState()

    var showLogFoodDialog by remember { mutableStateOf(false) }
    var selectedMealForDialog by remember { mutableStateOf(MealType.BREAKFAST) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var newGoalInput by remember { mutableStateOf(summary.dailyCalorieGoal.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nutrition & Meals", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = { showGoalDialog = true }) {
                        Text("Edit Target")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    selectedMealForDialog = MealType.BREAKFAST
                    showLogFoodDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Log Food") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Calorie Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Daily Calorie Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Goal", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "${summary.dailyCalorieGoal} kcal",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Consumed", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "${summary.totalCaloriesConsumed} kcal",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Remaining", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "${summary.caloriesRemaining} kcal",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val progress = (summary.totalCaloriesConsumed.toFloat() / summary.dailyCalorieGoal.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                }
            }

            // 2. Macro Progress Card (Protein, Carbs, Fats)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Macronutrients Target",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MacroRow(
                        title = "Protein",
                        consumed = summary.totalProtein,
                        target = summary.proteinGoalGrams,
                        unit = "g",
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MacroRow(
                        title = "Carbs",
                        consumed = summary.totalCarbs,
                        target = summary.carbsGoalGrams,
                        unit = "g",
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MacroRow(
                        title = "Fats",
                        consumed = summary.totalFat,
                        target = summary.fatGoalGrams,
                        unit = "g",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // 3. Meals Breakdown Cards
            Text(
                text = "Today's Meals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            MealType.values().forEach { mealType ->
                val mealsForCategory = summary.loggedMeals.filter { it.mealType == mealType }
                val categoryCalories = mealsForCategory.sumOf { it.calories }

                MealCategoryCard(
                    mealType = mealType,
                    totalCalories = categoryCalories,
                    items = mealsForCategory,
                    onAddClick = {
                        selectedMealForDialog = mealType
                        showLogFoodDialog = true
                    },
                    onDeleteClick = { mealId ->
                        nutritionViewModel.deleteLoggedMeal(mealId)
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Log Food Dialog
    if (showLogFoodDialog) {
        LogFoodDialog(
            presetFoods = nutritionViewModel.presetFoods,
            initialMealType = selectedMealForDialog,
            onLogPreset = { preset, mealType ->
                nutritionViewModel.logPresetFood(preset, mealType)
            },
            onLogCustom = { name, mealType, cals, prot, carbs, fat ->
                nutritionViewModel.logCustomFood(name, mealType, cals, prot, carbs, fat)
            },
            onDismiss = { showLogFoodDialog = false }
        )
    }

    // Edit Calorie Goal Dialog
    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Update Calorie Target") },
            text = {
                OutlinedTextField(
                    value = newGoalInput,
                    onValueChange = { newGoalInput = it },
                    label = { Text("Daily Goal (kcal)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = newGoalInput.toIntOrNull()
                        if (parsed != null && parsed in 1000..10000) {
                            nutritionViewModel.updateCalorieGoal(parsed)
                            Toast.makeText(context, "Calorie target updated!", Toast.LENGTH_SHORT).show()
                            showGoalDialog = false
                        } else {
                            Toast.makeText(context, "Please enter a valid goal (1000-10000 kcal)", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MacroRow(
    title: String,
    consumed: Int,
    target: Int,
    unit: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$consumed / $target $unit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (consumed.toFloat() / target.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun MealCategoryCard(
    mealType: MealType,
    totalCalories: Int,
    items: List<LoggedMeal>,
    onAddClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mealType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$totalCalories kcal",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add Food")
                    }
                }
            }

            if (items.isEmpty()) {
                Text(
                    text = "No items logged yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                items.forEach { meal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = meal.foodName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${meal.calories} kcal • P: ${meal.proteinGrams}g | C: ${meal.carbsGrams}g | F: ${meal.fatGrams}g",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDeleteClick(meal.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
