package com.codebuzz.app.bellyreset.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.codebuzz.app.bellyreset.R
import com.codebuzz.app.bellyreset.data.model.WATER_GOAL_LITERS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BellyResetTopBar(
    title: String,
    showLogo: Boolean = false
) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showLogo) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(title)
            }
        }
    )
}

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun AdherenceRing(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 132.dp
) {
    val sweepAngle by animateFloatAsState(
        targetValue = (score.toFloat() / 100f) * 360f,
        label = "Adherence Progress"
    )
    val trackColor = LocalContentColor.current.copy(alpha = 0.12f)
    val progressColor = MaterialTheme.colorScheme.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clearAndSetSemantics { contentDescription = "$score percent of today's goals done" }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            // Inset by half the stroke so the ring stays inside its bounds
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(this.size.width - strokeWidth, this.size.height - strokeWidth)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$score%",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WaterRow(
    liters: Double,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]
    val goalReached = liters >= WATER_GOAL_LITERS

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (goalReached) Icons.Rounded.CheckCircle else Icons.Rounded.WaterDrop,
            contentDescription = if (goalReached) "Goal reached" else null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Water",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = String.format(locale, "%.2f / %.1f L", liters, WATER_GOAL_LITERS),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        FilledTonalIconButton(onClick = onRemove, enabled = liters > 0.0) {
            Icon(Icons.Rounded.Remove, contentDescription = "Remove 250 ml")
        }
        Spacer(modifier = Modifier.width(8.dp))
        FilledIconButton(onClick = onAdd) {
            Icon(Icons.Rounded.Add, contentDescription = "Add 250 ml")
        }
    }
}

@Composable
fun ChecklistRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .toggleable(
                value = checked,
                onValueChange = { onToggle() },
                role = Role.Checkbox
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (checked) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HabitChips(
    habits: List<String>,
    completedHabits: List<String>,
    onHabitToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
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
                } else null
            )
        }
    }
}
