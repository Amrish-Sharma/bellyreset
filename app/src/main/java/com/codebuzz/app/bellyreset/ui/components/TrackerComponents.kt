package com.codebuzz.app.bellyreset.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.codebuzz.app.bellyreset.data.model.Meal
import com.codebuzz.app.bellyreset.ui.theme.Pine
import com.codebuzz.app.bellyreset.ui.theme.Turmeric

@Composable
fun AdherenceRing(
    score: Int,
    modifier: Modifier = Modifier
) {
    val sweepAngle by animateFloatAsState(
        targetValue = (score.toFloat() / 100f) * 360f,
        label = "Adherence Progress"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawCircle(
                color = Turmeric.copy(alpha = 0.2f),
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = Pine,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score%",
                style = MaterialTheme.typography.displayMedium,
                color = Pine,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Adherence",
                style = MaterialTheme.typography.labelLarge,
                color = Pine.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun WaterTracker(
    liters: Double,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Water Intake",
                    style = MaterialTheme.typography.titleMedium,
                    color = Pine
                )
                Text(
                    text = String.format(locale, "%.1f L / 2.0 L", liters),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Pine.copy(alpha = 0.7f)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Decrease", tint = Pine)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onAdd,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Turmeric)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Increase", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun MealChecklist(
    meals: List<Meal>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Meal Checklist",
            style = MaterialTheme.typography.titleMedium,
            color = Pine,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        meals.forEach { meal ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = meal.isCompleted,
                        onValueChange = { onToggle(meal.name) },
                        role = Role.Checkbox
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = meal.isCompleted,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(checkedColor = Pine)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Pine
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HabitToggles(
    movementCompleted: Boolean,
    onMovementToggle: () -> Unit,
    habits: List<String>,
    completedHabits: List<String>,
    onHabitToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Movement & Habits",
            style = MaterialTheme.typography.titleMedium,
            color = Pine,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        FilterChip(
            selected = movementCompleted,
            onClick = onMovementToggle,
            label = { Text("Daily Movement") },
            leadingIcon = if (movementCompleted) {
                { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Turmeric,
                selectedLabelColor = Color.White,
                selectedLeadingIconColor = Color.White
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            habits.forEach { habit ->
                val isSelected = completedHabits.contains(habit)
                FilterChip(
                    selected = isSelected,
                    onClick = { onHabitToggle(habit) },
                    label = { Text(habit) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Pine,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )
            }
        }
    }
}
