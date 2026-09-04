package com.codebuzz.app.bellyreset.ui.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.codebuzz.app.bellyreset.R
import com.codebuzz.app.bellyreset.ui.components.*
import com.codebuzz.app.bellyreset.ui.tracker.TrackerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TrackerViewModel,
    modifier: Modifier = Modifier
) {
    val record by viewModel.todayRecord.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logo),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Belly Reset")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 32.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AdherenceRing(score = record.adherenceScore)
                }
            }
            
            item {
                WaterTracker(
                    liters = record.waterIntakeLiters,
                    onAdd = { viewModel.updateWaterIntake(0.25) },
                    onRemove = { viewModel.updateWaterIntake(-0.25) }
                )
            }
            
            item {
                MealChecklist(
                    meals = record.meals,
                    onToggle = { viewModel.toggleMeal(it) }
                )
            }
            
            item(span = { GridItemSpan(maxLineSpan) }) {
                HabitToggles(
                    movementCompleted = record.movementCompleted,
                    onMovementToggle = { viewModel.toggleMovement() },
                    habits = listOf("Probiotics", "No Sugar", "Deep Breathing"),
                    completedHabits = record.habitsCompleted,
                    onHabitToggle = { viewModel.toggleHabit(it) }
                )
            }
        }
    }
}
