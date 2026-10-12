package com.codebuzz.app.bellyreset.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.codebuzz.app.bellyreset.data.datastore.TrackerSerializer
import com.codebuzz.app.bellyreset.data.model.AppSettings
import com.codebuzz.app.bellyreset.data.model.DailyRecord
import com.codebuzz.app.bellyreset.data.model.MetricEntry
import com.codebuzz.app.bellyreset.data.model.TrackerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

val Context.trackerDataStore: DataStore<TrackerState> by dataStore(
    fileName = "tracker_state.json",
    serializer = TrackerSerializer
)

class TrackerRepository(private val context: Context) {

    val trackerState: Flow<TrackerState> = context.trackerDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(TrackerState())
            } else {
                throw exception
            }
        }

    suspend fun updateDailyRecord(date: String, record: DailyRecord) {
        context.trackerDataStore.updateData { currentState ->
            val updatedRecords = currentState.dailyRecords.toMutableMap()
            updatedRecords[date] = record
            currentState.copy(dailyRecords = updatedRecords)
        }
    }

    suspend fun addMetricEntry(entry: MetricEntry) {
        context.trackerDataStore.updateData { currentState ->
            // One entry per day: logging again on the same day updates that day's values
            val existing = currentState.metrics.find { it.date == entry.date }
            val merged = existing?.copy(
                waistCircumferenceCm = entry.waistCircumferenceCm ?: existing.waistCircumferenceCm,
                weightKg = entry.weightKg ?: existing.weightKg
            ) ?: entry
            currentState.copy(metrics = currentState.metrics.filter { it.date != entry.date } + merged)
        }
    }

    suspend fun updateSettings(transform: (AppSettings) -> AppSettings) {
        context.trackerDataStore.updateData { currentState ->
            currentState.copy(settings = transform(currentState.settings))
        }
    }

    suspend fun clearAllData() {
        context.trackerDataStore.updateData {
            TrackerState()
        }
    }
}
