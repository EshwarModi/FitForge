package com.fitforge.app.viewmodel

import androidx.lifecycle.ViewModel
import com.fitforge.app.data.model.DashboardMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val _dashboardMetrics = MutableStateFlow(DashboardMetrics())
    val dashboardMetrics: StateFlow<DashboardMetrics> = _dashboardMetrics.asStateFlow()

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
    }

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        return sdf.format(Date())
    }

    fun addWater(amountLiters: Double = 0.25) {
        val current = _dashboardMetrics.value
        val newWater = (current.waterIntakeLiters + amountLiters).coerceAtMost(current.waterTargetLiters + 2.0)
        _dashboardMetrics.value = current.copy(waterIntakeLiters = newWater)
    }

    fun logWorkoutCompleted() {
        val current = _dashboardMetrics.value
        _dashboardMetrics.value = current.copy(
            todayWorkoutCompleted = (current.todayWorkoutCompleted + 1).coerceAtMost(current.todayWorkoutTotal),
            caloriesBurned = current.caloriesBurned + 250
        )
    }

    fun updateWeight(weightKg: Double) {
        val current = _dashboardMetrics.value
        _dashboardMetrics.value = current.copy(currentWeightKg = weightKg)
    }
}
