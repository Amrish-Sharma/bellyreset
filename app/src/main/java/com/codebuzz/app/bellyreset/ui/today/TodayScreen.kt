package com.codebuzz.app.bellyreset.ui.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codebuzz.app.bellyreset.data.model.DailyRecord
import com.codebuzz.app.bellyreset.ui.components.*
import com.codebuzz.app.bellyreset.ui.tracker.TrackerViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(
    viewModel: TrackerViewModel,
    modifier: Modifier = Modifier
) {
    val record by viewModel.todayRecord.collectAsState()

    Scaffold(
        topBar = { BellyResetTopBar(title = "Belly Reset", showLogo = true) },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            // One column, capped in width so it stays a single focused list on tablets
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TodaySummary(record = record)

                SectionCard {
                    WaterRow(
                        liters = record.waterIntakeLiters,
                        onAdd = { viewModel.updateWaterIntake(0.25) },
                        onRemove = { viewModel.updateWaterIntake(-0.25) }
                    )
                    record.meals.forEach { meal ->
                        ChecklistRow(
                            label = meal.name,
                            checked = meal.isCompleted,
                            onToggle = { viewModel.toggleMeal(meal.name) }
                        )
                    }
                    ChecklistRow(
                        label = "Daily movement",
                        checked = record.movementCompleted,
                        onToggle = { viewModel.toggleMovement() }
                    )
                }

                Column {
                    Text(
                        text = "Extras (not scored)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    HabitChips(
                        habits = listOf("Probiotics", "No Sugar", "Deep Breathing"),
                        completedHabits = record.habitsCompleted,
                        onHabitToggle = { viewModel.toggleHabit(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TodaySummary(record: DailyRecord) {
    val date = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdherenceRing(score = record.adherenceScore, size = 112.dp)
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${record.goalsCompleted} of ${record.goalsTotal} goals done",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
