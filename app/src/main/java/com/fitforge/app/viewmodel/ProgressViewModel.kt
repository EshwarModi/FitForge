package com.fitforge.app.viewmodel

import androidx.lifecycle.ViewModel
import com.fitforge.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class WaterState(
    val dailyGoalLiters: Double = 2.5,
    val currentLiters: Double = 1.8,
    val logs: List<WaterLogEntry> = listOf(
        WaterLogEntry("wl_1", 0.5),
        WaterLogEntry("wl_2", 0.5),
        WaterLogEntry("wl_3", 0.5),
        WaterLogEntry("wl_4", 0.3)
    )
) {
    val progressPercentage: Int
        get() = ((currentLiters / dailyGoalLiters) * 100).toInt().coerceIn(0, 100)
}

data class AnalyticsData(
    val totalWorkouts: Int = 14,
    val totalCaloriesBurned: Int = 4850,
    val averageSteps: Int = 7840,
    val waterCompliancePercent: Int = 85,
    val weeklyWorkouts: List<Pair<String, Int>> = listOf(
        Pair("Mon", 1),
        Pair("Tue", 1),
        Pair("Wed", 0),
        Pair("Thu", 1),
        Pair("Fri", 1),
        Pair("Sat", 0),
        Pair("Sun", 1)
    ),
    val weeklyWeightTrend: List<Pair<String, Double>> = listOf(
        Pair("Mon", 63.2),
        Pair("Tue", 63.0),
        Pair("Wed", 62.8),
        Pair("Thu", 62.5),
        Pair("Fri", 62.4),
        Pair("Sat", 62.2),
        Pair("Sun", 62.0)
    )
)

class ProgressViewModel : ViewModel() {

    private val _waterState = MutableStateFlow(WaterState())
    val waterState: StateFlow<WaterState> = _waterState.asStateFlow()

    private val _bodyMetrics = MutableStateFlow(
        BodyMetrics(
            heightCm = 170.0,
            currentWeightKg = 62.0,
            targetWeightKg = 60.0,
            weightLogs = listOf(
                WeightLogEntry("wl_1", 63.5),
                WeightLogEntry("wl_2", 63.0),
                WeightLogEntry("wl_3", 62.5),
                WeightLogEntry("wl_4", 62.0)
            )
        )
    )
    val bodyMetrics: StateFlow<BodyMetrics> = _bodyMetrics.asStateFlow()

    private val _timeframe = MutableStateFlow(ProgressTimeframe.WEEKLY)
    val timeframe: StateFlow<ProgressTimeframe> = _timeframe.asStateFlow()

    private val _analytics = MutableStateFlow(AnalyticsData())
    val analytics: StateFlow<AnalyticsData> = _analytics.asStateFlow()

    fun selectTimeframe(tf: ProgressTimeframe) {
        _timeframe.value = tf
    }

    // Water Actions
    fun addWater(amountLiters: Double) {
        val current = _waterState.value
        val newAmount = (current.currentLiters + amountLiters).coerceAtLeast(0.0)
        val newLog = WaterLogEntry("wl_${System.currentTimeMillis()}", amountLiters)

        _waterState.value = current.copy(
            currentLiters = newAmount,
            logs = listOf(newLog) + current.logs
        )
    }

    fun removeWater(amountLiters: Double = 0.25) {
        val current = _waterState.value
        val newAmount = (current.currentLiters - amountLiters).coerceAtLeast(0.0)

        _waterState.value = current.copy(currentLiters = newAmount)
    }

    fun updateWaterGoal(newGoalLiters: Double) {
        val current = _waterState.value
        _waterState.value = current.copy(dailyGoalLiters = newGoalLiters)
    }

    // Body Metrics Actions
    fun updateHeight(heightCm: Double) {
        val current = _bodyMetrics.value
        _bodyMetrics.value = current.copy(heightCm = heightCm)
    }

    fun updateCurrentWeight(weightKg: Double) {
        val current = _bodyMetrics.value
        val newLog = WeightLogEntry("wlog_${System.currentTimeMillis()}", weightKg)
        _bodyMetrics.value = current.copy(
            currentWeightKg = weightKg,
            weightLogs = listOf(newLog) + current.weightLogs
        )
    }

    fun updateTargetWeight(targetKg: Double) {
        val current = _bodyMetrics.value
        _bodyMetrics.value = current.copy(targetWeightKg = targetKg)
    }
}
