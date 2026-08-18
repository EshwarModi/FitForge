package com.fitforge.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ActiveWorkoutState(
    val plan: WorkoutPlan? = null,
    val currentExerciseIndex: Int = 0,
    val currentSet: Int = 1,
    val isResting: Boolean = false,
    val restSecondsRemaining: Int = 0,
    val totalRestSeconds: Int = 60,
    val isWorkoutCompleted: Boolean = false,
    val elapsedSeconds: Int = 0
)

class WorkoutViewModel : ViewModel() {

    private val _selectedCategory = MutableStateFlow(MuscleCategory.ALL)
    val selectedCategory: StateFlow<MuscleCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeWorkout = MutableStateFlow<ActiveWorkoutState?>(null)
    val activeWorkout: StateFlow<ActiveWorkoutState?> = _activeWorkout.asStateFlow()

    private val _workoutHistory = MutableStateFlow<List<WorkoutHistoryItem>>(emptyList())
    val workoutHistory: StateFlow<List<WorkoutHistoryItem>> = _workoutHistory.asStateFlow()

    private var timerJob: Job? = null
    private var workoutDurationJob: Job? = null

    // Standard pre-populated exercises database
    val sampleExercises = listOf(
        // Chest
        Exercise(
            id = "ex_chest_1",
            name = "Barbell Bench Press",
            category = MuscleCategory.CHEST,
            targetMuscle = "Pectoralis Major, Anterior Deltoids, Triceps",
            difficulty = DifficultyLevel.INTERMEDIATE,
            equipment = "Barbell, Bench",
            instructions = listOf(
                "Lie back on a flat bench. Grip the barbell slightly wider than shoulder-width.",
                "Lower the bar slowly to your mid-chest.",
                "Press the bar upward explosively until arms are extended."
            ),
            defaultSets = 4,
            defaultReps = 10,
            restDurationSeconds = 90
        ),
        Exercise(
            id = "ex_chest_2",
            name = "Incline Dumbbell Press",
            category = MuscleCategory.CHEST,
            targetMuscle = "Upper Chest, Front Shoulders",
            difficulty = DifficultyLevel.INTERMEDIATE,
            equipment = "Dumbbells, Incline Bench",
            instructions = listOf(
                "Set bench to a 30-45 degree incline.",
                "Lower dumbbells to upper chest level with elbows at 45 degrees.",
                "Press dumbbells back up over chest until arms are locked out."
            ),
            defaultSets = 3,
            defaultReps = 12,
            restDurationSeconds = 60
        ),
        Exercise(
            id = "ex_chest_3",
            name = "Push-ups",
            category = MuscleCategory.CHEST,
            targetMuscle = "Chest, Core, Triceps",
            difficulty = DifficultyLevel.BEGINNER,
            equipment = "Bodyweight",
            instructions = listOf(
                "Place hands shoulder-width apart on the floor in plank position.",
                "Lower chest until almost touching the ground.",
                "Push back up to starting position maintaining straight spine."
            ),
            defaultSets = 3,
            defaultReps = 15,
            restDurationSeconds = 45
        ),

        // Back
        Exercise(
            id = "ex_back_1",
            name = "Lat Pulldown",
            category = MuscleCategory.BACK,
            targetMuscle = "Latissimus Dorsi, Biceps",
            difficulty = DifficultyLevel.BEGINNER,
            equipment = "Cable Machine",
            instructions = listOf(
                "Sit at pulldown station with wide grip on bar.",
                "Pull bar down toward upper chest while driving elbows back.",
                "Slowly return bar back to top stretch position."
            ),
            defaultSets = 4,
            defaultReps = 12,
            restDurationSeconds = 60
        ),
        Exercise(
            id = "ex_back_2",
            name = "Bent-Over Barbell Row",
            category = MuscleCategory.BACK,
            targetMuscle = "Middle Back, Rhomboids, Lats",
            difficulty = DifficultyLevel.INTERMEDIATE,
            equipment = "Barbell",
            instructions = listOf(
                "Hinge at hips with knees slightly bent and back flat.",
                "Pull bar towards lower abdomen, squeezing shoulder blades together.",
                "Lower weight under control."
            ),
            defaultSets = 4,
            defaultReps = 10,
            restDurationSeconds = 90
        ),

        // Legs
        Exercise(
            id = "ex_leg_1",
            name = "Barbell Back Squat",
            category = MuscleCategory.LEGS,
            targetMuscle = "Quadriceps, Glutes, Hamstrings",
            difficulty = DifficultyLevel.INTERMEDIATE,
            equipment = "Barbell, Squat Rack",
            instructions = listOf(
                "Rest barbell across upper traps. Stand feet shoulder-width apart.",
                "Lower hips down and back until thighs are parallel to ground.",
                "Drive through heels to return to standing position."
            ),
            defaultSets = 4,
            defaultReps = 10,
            restDurationSeconds = 90
        ),
        Exercise(
            id = "ex_leg_2",
            name = "Romanian Deadlift",
            category = MuscleCategory.LEGS,
            targetMuscle = "Hamstrings, Glutes, Lower Back",
            difficulty = DifficultyLevel.INTERMEDIATE,
            equipment = "Barbell or Dumbbells",
            instructions = listOf(
                "Stand tall holding weight in front of thighs.",
                "Hinge at hips, pushing glutes back until stretch is felt in hamstrings.",
                "Drive hips forward to return to standing."
            ),
            defaultSets = 3,
            defaultReps = 12,
            restDurationSeconds = 60
        ),

        // Shoulders
        Exercise(
            id = "ex_shoulder_1",
            name = "Overhead Dumbbell Press",
            category = MuscleCategory.SHOULDERS,
            targetMuscle = "Anterior & Lateral Deltoids, Triceps",
            difficulty = DifficultyLevel.BEGINNER,
            equipment = "Dumbbells, Bench",
            instructions = listOf(
                "Hold dumbbells at shoulder height with palms facing forward.",
                "Press overhead until arms are fully extended.",
                "Lower back to shoulder height."
            ),
            defaultSets = 3,
            defaultReps = 12,
            restDurationSeconds = 60
        ),
        Exercise(
            id = "ex_shoulder_2",
            name = "Lateral Raises",
            category = MuscleCategory.SHOULDERS,
            targetMuscle = "Side Deltoids",
            difficulty = DifficultyLevel.BEGINNER,
            equipment = "Dumbbells",
            instructions = listOf(
                "Stand holding light dumbbells at sides.",
                "Raise arms out to sides until parallel to floor.",
                "Lower slowly under control."
            ),
            defaultSets = 3,
            defaultReps = 15,
            restDurationSeconds = 45
        ),

        // Arms
        Exercise(
            id = "ex_arm_1",
            name = "Bicep Dumbbell Curls",
            category = MuscleCategory.ARMS,
            targetMuscle = "Biceps Brachii",
            difficulty = DifficultyLevel.BEGINNER,
            equipment = "Dumbbells",
            instructions = listOf(
                "Stand with dumbbells at sides, palms facing forward.",
                "Curl weights upward while keeping upper arms stationary.",
                "Squeeze biceps at top and lower slowly."
            ),
            defaultSets = 3,
            defaultReps = 12,
            restDurationSeconds = 45
        ),
        Exercise(
            id = "ex_arm_2",
            name = "Tricep Rope Pushdown",
            category = MuscleCategory.ARMS,
            targetMuscle = "Triceps Lateral & Medial Heads",
            difficulty = DifficultyLevel.BEGINNER,
            equipment = "Cable Machine, Rope Attachment",
            instructions = listOf(
                "Hold rope with elbows tucked into sides.",
                "Extend arms downward, spreading rope ends apart at bottom.",
                "Return slowly to 90 degree elbow bend."
            ),
            defaultSets = 3,
            defaultReps = 15,
            restDurationSeconds = 45
        ),

        // Core
        Exercise(
            id = "ex_core_1",
            name = "Plank Hold",
            category = MuscleCategory.CORE,
            targetMuscle = "Rectus Abdominis, Transverse Abdominis",
            difficulty = DifficultyLevel.BEGINNER,
            equipment = "Bodyweight, Mat",
            instructions = listOf(
                "Hold forearm plank position with elbows under shoulders.",
                "Engage core and glutes to keep body in a straight line.",
                "Hold for target time without letting lower back sag."
            ),
            defaultSets = 3,
            defaultReps = 45, // seconds
            restDurationSeconds = 45
        ),

        // Full Body & Cardio
        Exercise(
            id = "ex_full_1",
            name = "Burpees",
            category = MuscleCategory.FULL_BODY,
            targetMuscle = "Full Body Conditioning",
            difficulty = DifficultyLevel.INTERMEDIATE,
            equipment = "Bodyweight",
            instructions = listOf(
                "From standing, drop into squat and place hands on floor.",
                "Kick feet back into push-up position, perform push-up.",
                "Jump feet back into squat and explode upward into jump."
            ),
            defaultSets = 4,
            defaultReps = 15,
            restDurationSeconds = 45
        ),
        Exercise(
            id = "ex_cardio_1",
            name = "High Intensity Mountain Climbers",
            category = MuscleCategory.CARDIO,
            targetMuscle = "Core, Cardio System",
            difficulty = DifficultyLevel.BEGINNER,
            equipment = "Bodyweight",
            instructions = listOf(
                "Start in push-up plank position.",
                "Drive right knee towards chest, then switch rapidly with left knee.",
                "Maintain fast pace while holding strong core."
            ),
            defaultSets = 4,
            defaultReps = 30,
            restDurationSeconds = 30
        )
    )

    val samplePlans = listOf(
        WorkoutPlan(
            id = "plan_full_body",
            title = "Full Body Blitz",
            description = "High-energy workout covering all major muscle groups for maximum calorie burn.",
            category = MuscleCategory.FULL_BODY,
            difficulty = DifficultyLevel.INTERMEDIATE,
            estimatedMinutes = 35,
            exercises = listOf(
                sampleExercises.first { it.id == "ex_full_1" },
                sampleExercises.first { it.id == "ex_chest_3" },
                sampleExercises.first { it.id == "ex_leg_1" },
                sampleExercises.first { it.id == "ex_core_1" }
            )
        ),
        WorkoutPlan(
            id = "plan_chest_triceps",
            title = "Chest & Arms Sculpt",
            description = "Targeted upper body push workout for chest, shoulders, and triceps.",
            category = MuscleCategory.CHEST,
            difficulty = DifficultyLevel.INTERMEDIATE,
            estimatedMinutes = 45,
            exercises = listOf(
                sampleExercises.first { it.id == "ex_chest_1" },
                sampleExercises.first { it.id == "ex_chest_2" },
                sampleExercises.first { it.id == "ex_shoulder_1" },
                sampleExercises.first { it.id == "ex_arm_2" }
            )
        ),
        WorkoutPlan(
            id = "plan_leg_day",
            title = "Leg Day Power",
            description = "Build lower body strength and explosive power with compound leg movements.",
            category = MuscleCategory.LEGS,
            difficulty = DifficultyLevel.ADVANCED,
            estimatedMinutes = 40,
            exercises = listOf(
                sampleExercises.first { it.id == "ex_leg_1" },
                sampleExercises.first { it.id == "ex_leg_2" }
            )
        ),
        WorkoutPlan(
            id = "plan_cardio_hiit",
            title = "Cardio HIIT Burner",
            description = "Fast-paced interval training designed to spike heart rate and boost endurance.",
            category = MuscleCategory.CARDIO,
            difficulty = DifficultyLevel.BEGINNER,
            estimatedMinutes = 20,
            exercises = listOf(
                sampleExercises.first { it.id == "ex_cardio_1" },
                sampleExercises.first { it.id == "ex_full_1" },
                sampleExercises.first { it.id == "ex_core_1" }
            )
        )
    )

    fun selectCategory(category: MuscleCategory) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredExercises(): List<Exercise> {
        val cat = _selectedCategory.value
        val query = _searchQuery.value.trim().lowercase()

        return sampleExercises.filter { ex ->
            val matchesCategory = (cat == MuscleCategory.ALL || ex.category == cat)
            val matchesSearch = query.isEmpty() ||
                    ex.name.lowercase().contains(query) ||
                    ex.targetMuscle.lowercase().contains(query)
            matchesCategory && matchesSearch
        }
    }

    fun getFilteredPlans(): List<WorkoutPlan> {
        val cat = _selectedCategory.value
        return if (cat == MuscleCategory.ALL) {
            samplePlans
        } else {
            samplePlans.filter { it.category == cat || it.category == MuscleCategory.FULL_BODY }
        }
    }

    // Active Workout Controls
    fun startWorkoutPlan(plan: WorkoutPlan) {
        timerJob?.cancel()
        workoutDurationJob?.cancel()

        _activeWorkout.value = ActiveWorkoutState(
            plan = plan,
            currentExerciseIndex = 0,
            currentSet = 1,
            isResting = false,
            isWorkoutCompleted = false,
            elapsedSeconds = 0
        )

        startDurationTimer()
    }

    private fun startDurationTimer() {
        workoutDurationJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeWorkout.value?.let { current ->
                    if (!current.isWorkoutCompleted) {
                        _activeWorkout.value = current.copy(elapsedSeconds = current.elapsedSeconds + 1)
                    }
                }
            }
        }
    }

    fun completeCurrentSet() {
        val current = _activeWorkout.value ?: return
        val currentExercise = current.plan?.exercises?.getOrNull(current.currentExerciseIndex) ?: return

        if (current.currentSet < currentExercise.defaultSets) {
            // Move to next set and start rest timer
            startRestTimer(currentExercise.restDurationSeconds, nextSet = current.currentSet + 1, nextIndex = current.currentExerciseIndex)
        } else {
            // Finished all sets for this exercise! Check if more exercises remain
            val nextIndex = current.currentExerciseIndex + 1
            if (nextIndex < (current.plan?.exercises?.size ?: 0)) {
                val nextExercise = current.plan?.exercises?.get(nextIndex)
                startRestTimer(nextExercise?.restDurationSeconds ?: 60, nextSet = 1, nextIndex = nextIndex)
            } else {
                // Workout Finished!
                finishWorkout()
            }
        }
    }

    private fun startRestTimer(restSeconds: Int, nextSet: Int, nextIndex: Int) {
        timerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value?.copy(
            isResting = true,
            restSecondsRemaining = restSeconds,
            totalRestSeconds = restSeconds,
            currentSet = nextSet,
            currentExerciseIndex = nextIndex
        )

        timerJob = viewModelScope.launch {
            var remaining = restSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _activeWorkout.value = _activeWorkout.value?.copy(restSecondsRemaining = remaining)
            }
            // Rest ended
            _activeWorkout.value = _activeWorkout.value?.copy(isResting = false)
        }
    }

    fun skipRestTimer() {
        timerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value?.copy(isResting = false, restSecondsRemaining = 0)
    }

    fun addRestTime(additionalSeconds: Int = 10) {
        _activeWorkout.value?.let { current ->
            if (current.isResting) {
                val newRemaining = current.restSecondsRemaining + additionalSeconds
                _activeWorkout.value = current.copy(
                    restSecondsRemaining = newRemaining,
                    totalRestSeconds = current.totalRestSeconds + additionalSeconds
                )
            }
        }
    }

    fun finishWorkout() {
        timerJob?.cancel()
        workoutDurationJob?.cancel()

        _activeWorkout.value?.let { current ->
            val plan = current.plan ?: return
            val durationMins = (current.elapsedSeconds / 60).coerceAtLeast(1)
            val calories = durationMins * 10

            val historyItem = WorkoutHistoryItem(
                id = "hist_${System.currentTimeMillis()}",
                planTitle = plan.title,
                category = plan.category.displayName,
                durationMinutes = durationMins,
                caloriesBurned = calories
            )

            _workoutHistory.value = listOf(historyItem) + _workoutHistory.value

            _activeWorkout.value = current.copy(
                isResting = false,
                isWorkoutCompleted = true
            )
        }
    }

    fun closeActiveWorkout() {
        timerJob?.cancel()
        workoutDurationJob?.cancel()
        _activeWorkout.value = null
    }
}
