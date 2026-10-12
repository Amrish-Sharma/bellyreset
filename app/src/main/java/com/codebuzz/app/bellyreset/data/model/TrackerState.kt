package com.codebuzz.app.bellyreset.data.model

import kotlinx.serialization.Serializable

const val WATER_GOAL_LITERS = 2.0

@Serializable
data class TrackerState(
    val dailyRecords: Map<String, DailyRecord> = emptyMap(), // Key is date in ISO format (YYYY-MM-DD)
    val metrics: List<MetricEntry> = emptyList(),
    val settings: AppSettings = AppSettings()
)

@Serializable
data class AppSettings(
    val remindersEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

@Serializable
enum class ThemeMode { SYSTEM, LIGHT, DARK }

@Serializable
data class DailyRecord(
    val adherenceScore: Int = 0,
    val waterIntakeLiters: Double = 0.0,
    val meals: List<Meal> = listOf(
        Meal("Breakfast"),
        Meal("Lunch"),
        Meal("Dinner"),
        Meal("Snacks")
    ),
    val movementCompleted: Boolean = false,
    val habitsCompleted: List<String> = emptyList()
) {
    // Goals that count toward the adherence score: meals + movement + water goal
    val goalsTotal: Int
        get() = meals.size + 2

    val goalsCompleted: Int
        get() = meals.count { it.isCompleted } +
            (if (movementCompleted) 1 else 0) +
            (if (waterIntakeLiters >= WATER_GOAL_LITERS) 1 else 0)
}

@Serializable
data class Meal(
    val name: String,
    val description: String = "",
    val time: String = "",
    val isCompleted: Boolean = false
)

@Serializable
data class MetricEntry(
    val date: String,
    val waistCircumferenceCm: Double? = null,
    val weightKg: Double? = null
)
