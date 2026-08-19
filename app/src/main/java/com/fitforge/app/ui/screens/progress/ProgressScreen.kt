package com.fitforge.app.ui.screens.progress

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitforge.app.data.model.ProgressTimeframe
import com.fitforge.app.viewmodel.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    progressViewModel: ProgressViewModel = viewModel()
) {
    val context = LocalContext.current
    val waterState by progressViewModel.waterState.collectAsState()
    val metrics by progressViewModel.bodyMetrics.collectAsState()
    val timeframe by progressViewModel.timeframe.collectAsState()
    val analytics by progressViewModel.analytics.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Analytics, 1 = Water Tracker, 2 = BMI & Metrics

    // Dialog states
    var showWaterGoalDialog by remember { mutableStateOf(false) }
    var waterGoalInput by remember { mutableStateOf(waterState.dailyGoalLiters.toString()) }

    var showEditMetricsDialog by remember { mutableStateOf(false) }
    var heightInput by remember { mutableStateOf(metrics.heightCm.toString()) }
    var weightInput by remember { mutableStateOf(metrics.currentWeightKg.toString()) }
    var targetWeightInput by remember { mutableStateOf(metrics.targetWeightKg.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress & Body Metrics", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Main Section Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Analytics") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Water Tracker") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("BMI & Metrics") }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> AnalyticsTabContent(
                        timeframe = timeframe,
                        analytics = analytics,
                        onTimeframeSelected = { progressViewModel.selectTimeframe(it) }
                    )
                    1 -> WaterTrackerTabContent(
                        waterState = waterState,
                        onAddWater = { progressViewModel.addWater(it) },
                        onRemoveWater = { progressViewModel.removeWater(it) },
                        onEditGoalClick = { showWaterGoalDialog = true }
                    )
                    2 -> BmiMetricsTabContent(
                        metrics = metrics,
                        onEditMetricsClick = { showEditMetricsDialog = true }
                    )
                }
            }
        }
    }

    // Water Goal Dialog
    if (showWaterGoalDialog) {
        AlertDialog(
            onDismissRequest = { showWaterGoalDialog = false },
            title = { Text("Set Daily Water Goal (Liters)") },
            text = {
                OutlinedTextField(
                    value = waterGoalInput,
                    onValueChange = { waterGoalInput = it },
                    label = { Text("Goal (L)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = waterGoalInput.toDoubleOrNull()
                        if (parsed != null && parsed in 1.0..10.0) {
                            progressViewModel.updateWaterGoal(parsed)
                            Toast.makeText(context, "Water goal updated!", Toast.LENGTH_SHORT).show()
                            showWaterGoalDialog = false
                        } else {
                            Toast.makeText(context, "Please enter a valid goal (1.0 - 10.0 L)", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWaterGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Body Metrics Dialog
    if (showEditMetricsDialog) {
        AlertDialog(
            onDismissRequest = { showEditMetricsDialog = false },
            title = { Text("Update Height & Weight") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { heightInput = it },
                        label = { Text("Height (cm)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Current Weight (kg)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = targetWeightInput,
                        onValueChange = { targetWeightInput = it },
                        label = { Text("Target Weight (kg)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val h = heightInput.toDoubleOrNull()
                        val w = weightInput.toDoubleOrNull()
                        val tw = targetWeightInput.toDoubleOrNull()

                        if (h != null && h in 50.0..250.0 && w != null && w in 20.0..300.0) {
                            progressViewModel.updateHeight(h)
                            progressViewModel.updateCurrentWeight(w)
                            if (tw != null && tw in 20.0..300.0) {
                                progressViewModel.updateTargetWeight(tw)
                            }
                            Toast.makeText(context, "Body metrics updated!", Toast.LENGTH_SHORT).show()
                            showEditMetricsDialog = false
                        } else {
                            Toast.makeText(context, "Please enter valid height and weight values", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditMetricsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ----------------------------------------------------
// Tab 1: Analytics & Charts Content
// ----------------------------------------------------
@Composable
fun AnalyticsTabContent(
    timeframe: ProgressTimeframe,
    analytics: com.fitforge.app.viewmodel.AnalyticsData,
    onTimeframeSelected: (ProgressTimeframe) -> Unit
) {
    // Timeframe Chips
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProgressTimeframe.values().forEach { tf ->
            FilterChip(
                selected = timeframe == tf,
                onClick = { onTimeframeSelected(tf) },
                label = { Text(tf.displayName) }
            )
        }
    }

    // Analytics Grid
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Workouts",
            value = "${analytics.totalWorkouts}",
            subtitle = "Completed",
            icon = Icons.Default.FitnessCenter,
            color = MaterialTheme.colorScheme.primaryContainer
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Calories",
            value = "${analytics.totalCaloriesBurned}",
            subtitle = "kcal burned",
            icon = Icons.Default.LocalFireDepartment,
            color = MaterialTheme.colorScheme.errorContainer
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Avg Steps",
            value = String.format("%,d", analytics.averageSteps),
            subtitle = "steps / day",
            icon = Icons.Default.DirectionsWalk,
            color = MaterialTheme.colorScheme.secondaryContainer
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Water Goal",
            value = "${analytics.waterCompliancePercent}%",
            subtitle = "Achieved",
            icon = Icons.Default.WaterDrop,
            color = MaterialTheme.colorScheme.tertiaryContainer
        )
    }

    // Visual Chart Card: Workout Activity
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Workout Frequency (${timeframe.displayName})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                analytics.weeklyWorkouts.forEach { (day, count) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .fillMaxHeight(if (count > 0) 0.8f else 0.15f)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (count > 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Tab 2: Water Tracker Content
// ----------------------------------------------------
@Composable
fun WaterTrackerTabContent(
    waterState: com.fitforge.app.viewmodel.WaterState,
    onAddWater: (Double) -> Unit,
    onRemoveWater: (Double) -> Unit,
    onEditGoalClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Hydration Goal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                TextButton(onClick = onEditGoalClick) {
                    Text("Edit Goal")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Big Circular Meter
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = String.format("%.1f L", waterState.currentLiters),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "of ${waterState.dailyGoalLiters} L",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${waterState.progressPercentage}% Completed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Add / Remove Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { onAddWater(0.25) }) {
                    Text("+250 ml")
                }
                Button(onClick = { onAddWater(0.50) }) {
                    Text("+500 ml")
                }
                OutlinedButton(onClick = { onRemoveWater(0.25) }) {
                    Text("-250 ml")
                }
            }
        }
    }

    // Water Log History
    Text(
        text = "Today's Water Log",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (waterState.logs.isEmpty()) {
                Text("No water logged today yet.")
            } else {
                waterState.logs.forEach { log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Added ${log.amountLiters} Liters",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Logged",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Divider()
                }
            }
        }
    }
}

// ----------------------------------------------------
// Tab 3: BMI & Body Metrics Content
// ----------------------------------------------------
@Composable
fun BmiMetricsTabContent(
    metrics: com.fitforge.app.data.model.BodyMetrics,
    onEditMetricsClick: () -> Unit
) {
    // Calculated BMI Header Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Body Mass Index (BMI)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onEditMetricsClick) {
                    Text("Edit Metrics")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = String.format("%.1f", metrics.bmi),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = metrics.bmiCategory,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Height: ${metrics.heightCm} cm", style = MaterialTheme.typography.bodyMedium)
                    Text("Weight: ${metrics.currentWeightKg} kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("Target: ${metrics.targetWeightKg} kg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Recommendation",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = metrics.bmiDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Healthy Weight Range for your height: ${String.format("%.1f", metrics.minHealthyWeightKg)} kg - ${String.format("%.1f", metrics.maxHealthyWeightKg)} kg",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // Weight Goal Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Target Weight Goal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val diff = metrics.currentWeightKg - metrics.targetWeightKg
            val diffText = if (diff > 0) "${String.format("%.1f", diff)} kg to lose" else "${String.format("%.1f", -diff)} kg to gain"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current: ${metrics.currentWeightKg} kg ➔ Target: ${metrics.targetWeightKg} kg",
                    style = MaterialTheme.typography.bodyMedium
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = diffText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}
