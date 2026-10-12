package com.codebuzz.app.bellyreset.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import com.codebuzz.app.bellyreset.ui.components.BellyResetTopBar
import com.codebuzz.app.bellyreset.ui.components.SectionCard
import com.codebuzz.app.bellyreset.ui.tracker.TrackerViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ProgressScreen(viewModel: TrackerViewModel) {
    val metrics by viewModel.allMetrics.collectAsState()
    val streak by viewModel.streak.collectAsState()

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isTablet = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    var weightInput by rememberSaveable { mutableStateOf("") }
    var waistInput by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val weights = metrics.mapNotNull { it.weightKg }
    val waists = metrics.mapNotNull { it.waistCircumferenceCm }

    val logCard: @Composable () -> Unit = {
        LogMetricsCard(
            weightInput = weightInput,
            waistInput = waistInput,
            onWeightChange = { weightInput = it },
            onWaistChange = { waistInput = it },
            onLog = { weight, waist ->
                viewModel.logMetrics(waist, weight)
                weightInput = ""
                waistInput = ""
                keyboardController?.hide()
                scope.launch { snackbarHostState.showSnackbar("Measurements saved") }
            }
        )
    }

    Scaffold(
        topBar = { BellyResetTopBar(title = "Progress") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StreakCard(streak = streak)
                    logCard()
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricChart(
                        title = "Weight",
                        unit = "kg",
                        data = weights,
                        color = MaterialTheme.colorScheme.primary
                    )
                    MetricChart(
                        title = "Waist",
                        unit = "cm",
                        data = waists,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StreakCard(streak = streak)
                logCard()
                MetricChart(
                    title = "Weight",
                    unit = "kg",
                    data = weights,
                    color = MaterialTheme.colorScheme.primary
                )
                MetricChart(
                    title = "Waist",
                    unit = "cm",
                    data = waists,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

private fun parseMeasurement(input: String): Double? =
    input.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0.0 }

private fun filterMeasurementInput(input: String): String =
    input.filter { it.isDigit() || it == '.' || it == ',' }.take(6)

private fun formatValue(value: Double): String = String.format(Locale.getDefault(), "%.1f", value)

@Composable
fun LogMetricsCard(
    weightInput: String,
    waistInput: String,
    onWeightChange: (String) -> Unit,
    onWaistChange: (String) -> Unit,
    onLog: (Double?, Double?) -> Unit
) {
    val weight = parseMeasurement(weightInput)
    val waist = parseMeasurement(waistInput)
    val weightInvalid = weightInput.isNotBlank() && weight == null
    val waistInvalid = waistInput.isNotBlank() && waist == null
    val canLog = (weight != null || waist != null) && !weightInvalid && !waistInvalid
    val submit = { if (canLog) onLog(weight, waist) }

    SectionCard {
        Text(
            text = "Log today's measurements",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Fill in one or both. Logging again today replaces today's entry.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = weightInput,
                onValueChange = { onWeightChange(filterMeasurementInput(it)) },
                label = { Text("Weight") },
                suffix = { Text("kg") },
                isError = weightInvalid,
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = waistInput,
                onValueChange = { onWaistChange(filterMeasurementInput(it)) },
                label = { Text("Waist") },
                suffix = { Text("cm") },
                isError = waistInvalid,
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { submit() })
            )
        }

        if (weightInvalid || waistInvalid) {
            Text(
                text = "Enter a number greater than 0.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = submit,
            enabled = canLog,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Rounded.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save measurements")
        }
    }
}

@Composable
fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = when (streak) {
                        0 -> "No streak yet"
                        1 -> "1 day streak"
                        else -> "$streak day streak"
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = if (streak == 0) {
                        "Finish every goal today to start one."
                    } else {
                        "Finish every goal each day to keep it going."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MetricChart(title: String, unit: String, data: List<Double>, color: Color) {
    SectionCard {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (data.isEmpty()) {
            Text(
                text = "No entries yet. Log a measurement to start tracking.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            return@SectionCard
        }

        Text(
            text = "${formatValue(data.last())} $unit",
            style = MaterialTheme.typography.headlineSmall
        )

        if (data.size < 2) {
            Text(
                text = "Log one more entry to see your trend.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%+.1f %s since first entry",
                    data.last() - data.first(),
                    unit
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Sparkline(
                data = data,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Low ${formatValue(data.min())}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "High ${formatValue(data.max())}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun Sparkline(data: List<Double>, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val min = data.minOrNull() ?: 0.0
        val max = data.maxOrNull() ?: 1.0
        val range = if (max == min) 1.0 else max - min

        // Keep the line and end dot inside the canvas
        val inset = 5.dp.toPx()
        val width = size.width - inset * 2
        val height = size.height - inset * 2
        val stepX = width / (data.size - 1)

        val points = data.mapIndexed { index, value ->
            val x = inset + index * stepX
            val y = inset + height - ((value - min) / range * height).toFloat()
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        val fill = Path().apply {
            addPath(path)
            lineTo(points.last().x, size.height)
            lineTo(points.first().x, size.height)
            close()
        }

        drawPath(
            path = fill,
            brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.25f), Color.Transparent))
        )
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        drawCircle(color = color, radius = 5.dp.toPx(), center = points.last())
    }
}
