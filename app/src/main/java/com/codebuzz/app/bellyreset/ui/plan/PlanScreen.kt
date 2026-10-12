package com.codebuzz.app.bellyreset.ui.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codebuzz.app.bellyreset.ui.components.BellyResetTopBar

@Composable
fun PlanScreen() {
    val planPhases = listOf(
        PlanPhase("Phase 1: Preparation", "Get your body ready for the reset.", true),
        PlanPhase("Phase 2: Detox", "Eliminate inflammatory foods.", false),
        PlanPhase("Phase 3: Reintroduction", "Slowly bring back foods.", false),
        PlanPhase("Phase 4: Maintenance", "Sustainable habits for the long term.", false)
    )

    Scaffold(
        topBar = { BellyResetTopBar(title = "Your Plan") }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(planPhases) { phase ->
                PhaseItem(phase)
            }
        }
    }
}

data class PlanPhase(
    val title: String,
    val description: String,
    val isCompleted: Boolean
)

@Composable
fun PhaseItem(phase: PlanPhase) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (phase.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = if (phase.isCompleted) "Completed" else "Not completed",
                tint = if (phase.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = phase.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = phase.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
