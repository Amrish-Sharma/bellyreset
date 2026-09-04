package com.codebuzz.app.bellyreset.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackerState(
    val dailyRecords: Map<String, DailyRecord> = emptyMap(), // Key is date in ISO format (YYYY-MM-DD)
    val metrics: List<MetricEntry> = emptyList()
)

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
)

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
