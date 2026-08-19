package com.fitforge.app.ui.screens.nutrition

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fitforge.app.data.model.FoodPreset
import com.fitforge.app.data.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogFoodDialog(
    presetFoods: List<FoodPreset>,
    initialMealType: MealType = MealType.BREAKFAST,
    onLogPreset: (FoodPreset, MealType) -> Unit,
    onLogCustom: (String, MealType, Int, Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0 = Preset, 1 = Custom
    var activeMealType by remember { mutableStateOf(initialMealType) }

    // Custom fields
    var foodName by remember { mutableStateOf("") }
    var caloriesStr by remember { mutableStateOf("") }
    var proteinStr by remember { mutableStateOf("") }
    var carbsStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Log Food / Meal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Meal Category Selector Chips
                Text(
                    text = "Log under meal:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MealType.values().forEach { type ->
                        FilterChip(
                            selected = activeMealType == type,
                            onClick = { activeMealType = type },
                            label = { Text(type.displayName) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: Preset vs Custom
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Quick Presets") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Custom Entry") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == 0) {
                    // Presets List
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        presetFoods.forEach { preset ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onLogPreset(preset, activeMealType)
                                        Toast.makeText(context, "${preset.name} logged under ${activeMealType.displayName}! 🥗", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = preset.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${preset.servingSize} • ${preset.calories} kcal",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "P: ${preset.proteinGrams}g | C: ${preset.carbsGrams}g | F: ${preset.fatGrams}g",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            onLogPreset(preset, activeMealType)
                                            Toast.makeText(context, "${preset.name} logged under ${activeMealType.displayName}!", Toast.LENGTH_SHORT).show()
                                            onDismiss()
                                        }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Custom Entry Form
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = foodName,
                            onValueChange = { foodName = it },
                            label = { Text("Food Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = caloriesStr,
                            onValueChange = { caloriesStr = it },
                            label = { Text("Calories (kcal)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = proteinStr,
                                onValueChange = { proteinStr = it },
                                label = { Text("Protein (g)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = carbsStr,
                                onValueChange = { carbsStr = it },
                                label = { Text("Carbs (g)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = fatStr,
                                onValueChange = { fatStr = it },
                                label = { Text("Fat (g)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            onClick = {
                                val name = foodName.trim()
                                val cals = caloriesStr.toIntOrNull() ?: 0
                                val prot = proteinStr.toIntOrNull() ?: 0
                                val carbs = carbsStr.toIntOrNull() ?: 0
                                val fat = fatStr.toIntOrNull() ?: 0

                                if (name.isBlank()) {
                                    Toast.makeText(context, "Please enter food name", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (cals <= 0) {
                                    Toast.makeText(context, "Please enter valid calories", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                onLogCustom(name, activeMealType, cals, prot, carbs, fat)
                                Toast.makeText(context, "$name logged under ${activeMealType.displayName}!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        ) {
                            Text("LOG FOOD ITEM")
                        }
                    }
                }
            }
        }
    }
}
