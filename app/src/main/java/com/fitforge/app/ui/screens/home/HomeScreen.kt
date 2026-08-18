package com.fitforge.app.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitforge.app.data.model.DashboardMetrics
import com.fitforge.app.viewmodel.AuthViewModel
import com.fitforge.app.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToWorkout: () -> Unit = {},
    onNavigateToNutrition: () -> Unit = {},
    onNavigateToProgress: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val metrics by homeViewModel.dashboardMetrics.collectAsState()
    val greeting = homeViewModel.getGreeting()
    val dateString = homeViewModel.getFormattedDate()

    val userEmail = authViewModel.currentUserEmail() ?: "Athlete"
    val userName = userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

    var showWeightDialog by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf(metrics.currentWeightKg.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "$greeting, $userName 👋",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = userName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Today's Workout Feature Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToWorkout() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
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
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Today's Workout",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "${metrics.todayWorkoutCompleted} / ${metrics.todayWorkoutTotal} completed",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = metrics.todayWorkoutName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = {
                            if (metrics.todayWorkoutTotal > 0)
                                metrics.todayWorkoutCompleted.toFloat() / metrics.todayWorkoutTotal.toFloat()
                            else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (metrics.todayWorkoutCompleted < metrics.todayWorkoutTotal) {
                                homeViewModel.logWorkoutCompleted()
                                Toast.makeText(context, "Workout completed! Great job! 💪", Toast.LENGTH_SHORT).show()
                            } else {
                                onNavigateToWorkout()
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (metrics.todayWorkoutCompleted < metrics.todayWorkoutTotal) "Complete Workout" else "View Workouts"
                        )
                    }
                }
            }

            // 2. Metrics Grid (Calories, Steps, Water, Weight, BMI)
            Text(
                text = "Today's Metrics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Calories",
                    value = "${metrics.caloriesBurned}",
                    unit = " / ${metrics.caloriesTarget} kcal",
                    icon = Icons.Default.LocalFireDepartment,
                    progress = (metrics.caloriesBurned.toFloat() / metrics.caloriesTarget).coerceIn(0f, 1f),
                    color = MaterialTheme.colorScheme.errorContainer
                )

                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Steps",
                    value = String.format("%,d", metrics.stepsCount),
                    unit = " / 10k steps",
                    icon = Icons.Default.DirectionsWalk,
                    progress = (metrics.stepsCount.toFloat() / metrics.stepsTarget).coerceIn(0f, 1f),
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Water",
                    value = String.format("%.1f", metrics.waterIntakeLiters),
                    unit = " / ${metrics.waterTargetLiters} L",
                    icon = Icons.Default.WaterDrop,
                    progress = (metrics.waterIntakeLiters.toFloat() / metrics.waterTargetLiters.toFloat()).coerceIn(0f, 1f),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )

                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Weight & BMI",
                    value = "${metrics.currentWeightKg} kg",
                    unit = "BMI: ${String.format("%.1f", metrics.bmi)} (${metrics.bmiCategory})",
                    icon = Icons.Default.MonitorWeight,
                    progress = null,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // 3. Quick Actions Section
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionButton(
                    icon = Icons.Default.PlayArrow,
                    label = "Start Workout",
                    onClick = { onNavigateToWorkout() }
                )
                QuickActionButton(
                    icon = Icons.Default.Restaurant,
                    label = "Log Food",
                    onClick = { onNavigateToNutrition() }
                )
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Add Water",
                    onClick = {
                        homeViewModel.addWater(0.25)
                        Toast.makeText(context, "+250 ml water logged 💧", Toast.LENGTH_SHORT).show()
                    }
                )
                QuickActionButton(
                    icon = Icons.Default.Edit,
                    label = "Track Weight",
                    onClick = { showWeightDialog = true }
                )
            }

            // 4. Weekly Progress Card
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
                        Text(
                            text = "Weekly Activity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { onNavigateToProgress() }) {
                            Text("View All")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val days = listOf("M", "T", "W", "T", "F", "S", "S")
                    val completedDays = listOf(true, true, false, true, false, false, false)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        days.forEachIndexed { index, day ->
                            val isDone = completedDays.getOrElse(index) { false }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = day,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Update Weight Dialog
    if (showWeightDialog) {
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Update Weight (kg)") },
            text = {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = weightInput.toDoubleOrNull()
                        if (parsed != null && parsed in 20.0..300.0) {
                            homeViewModel.updateWeight(parsed)
                            Toast.makeText(context, "Weight updated! Current BMI: ${String.format("%.1f", metrics.bmi)}", Toast.LENGTH_SHORT).show()
                            showWeightDialog = false
                        } else {
                            Toast.makeText(context, "Please enter a valid weight (20 - 300 kg)", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    progress: Float?,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            if (progress != null) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}