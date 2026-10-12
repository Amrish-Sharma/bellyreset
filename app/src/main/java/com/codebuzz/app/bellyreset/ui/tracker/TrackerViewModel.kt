package com.codebuzz.app.bellyreset.ui.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebuzz.app.bellyreset.data.model.AppSettings
import com.codebuzz.app.bellyreset.data.model.DailyRecord
import com.codebuzz.app.bellyreset.data.model.ThemeMode
import com.codebuzz.app.bellyreset.data.model.Meal
import com.codebuzz.app.bellyreset.data.repository.TrackerRepository
import com.codebuzz.app.bellyreset.data.model.MetricEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TrackerViewModel(private val repository: TrackerRepository) : ViewModel() {

    private val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    val todayRecord: StateFlow<DailyRecord> = repository.trackerState
        .map { state ->
            state.dailyRecords[today] ?: DailyRecord()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyRecord()
        )

    val allMetrics: StateFlow<List<MetricEntry>> = repository.trackerState
        .map { it.metrics }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val streak: StateFlow<Int> = repository.trackerState
        .map { state ->
            calculateStreak(state.dailyRecords)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // null until the stored settings have been read
    val settings: StateFlow<AppSettings?> = repository.trackerState
        .map { it.settings }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(remindersEnabled = enabled) }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(themeMode = mode) }
        }
    }

    fun updateWaterIntake(delta: Double) {
        viewModelScope.launch {
            val currentRecord = todayRecord.value
            val newWater = (currentRecord.waterIntakeLiters + delta).coerceAtLeast(0.0)
            val updatedRecord = currentRecord.copy(waterIntakeLiters = newWater)
            repository.updateDailyRecord(today, updatedRecord.calculateAdherence())
        }
    }

    fun logMetrics(waist: Double?, weight: Double?) {
        viewModelScope.launch {
            repository.addMetricEntry(
                MetricEntry(
                    date = today,
                    waistCircumferenceCm = waist,
                    weightKg = weight
                )
            )
        }
    }

    private fun calculateStreak(records: Map<String, DailyRecord>): Int {
        if (records.isEmpty()) return 0
        
        var currentStreak = 0
        var checkDate = LocalDate.now()
        
        // If today isn't 100% yet, check from yesterday
        val todayRecord = records[checkDate.format(DateTimeFormatter.ISO_LOCAL_DATE)]
        if (todayRecord == null || todayRecord.adherenceScore < 100) {
            checkDate = checkDate.minusDays(1)
        }

        while (true) {
            val dateStr = checkDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val record = records[dateStr]
            if (record != null && record.adherenceScore == 100) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }
        return currentStreak
    }

    fun toggleMeal(mealName: String) {
        viewModelScope.launch {
            val currentRecord = todayRecord.value
            val updatedMeals = currentRecord.meals.map {
                if (it.name == mealName) it.copy(isCompleted = !it.isCompleted) else it
            }
            val updatedRecord = currentRecord.copy(meals = updatedMeals)
            repository.updateDailyRecord(today, updatedRecord.calculateAdherence())
        }
    }

    fun toggleMovement() {
        viewModelScope.launch {
            val currentRecord = todayRecord.value
            val updatedRecord = currentRecord.copy(movementCompleted = !currentRecord.movementCompleted)
            repository.updateDailyRecord(today, updatedRecord.calculateAdherence())
        }
    }

    fun toggleHabit(habitName: String) {
        viewModelScope.launch {
            val currentRecord = todayRecord.value
            val updatedHabits = if (currentRecord.habitsCompleted.contains(habitName)) {
                currentRecord.habitsCompleted.filter { it != habitName }
            } else {
                currentRecord.habitsCompleted + habitName
            }
            val updatedRecord = currentRecord.copy(habitsCompleted = updatedHabits)
            repository.updateDailyRecord(today, updatedRecord.calculateAdherence())
        }
    }

    private fun DailyRecord.calculateAdherence(): DailyRecord {
        val score = (goalsCompleted.toFloat() / goalsTotal.toFloat() * 100).toInt()
        return this.copy(adherenceScore = score)
    }
}
