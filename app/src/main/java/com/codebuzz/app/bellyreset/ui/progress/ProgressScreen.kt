package com.codebuzz.app.bellyreset.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import com.codebuzz.app.bellyreset.data.model.MetricEntry
import com.codebuzz.app.bellyreset.ui.theme.Pine
import com.codebuzz.app.bellyreset.ui.theme.Turmeric
import com.codebuzz.app.bellyreset.ui.tracker.TrackerViewModel

@Composable
fun ProgressScreen(viewModel: TrackerViewModel) {
    val metrics by viewModel.allMetrics.collectAsState()
    val streak by viewModel.streak.collectAsState()
    
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isTablet = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    var weightInput by remember { mutableStateOf("") }
    var waistInput by remember { mutableStateOf("") }

    if (isTablet) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StreakCard(streak = streak)
                LogMetricsCard(
                    weightInput = weightInput,
                    waistInput = waistInput,
                    onWeightChange = { weightInput = it },
                    onWaistChange = { waistInput = it },
                    onLog = { weight, waist ->
                        viewModel.logMetrics(waist, weight)
                        weightInput = ""
                        waistInput = ""
                    }
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MetricChart(
                    title = "Weight Trend (kg)",
                    data = metrics.mapNotNull { it.weightKg },
                    color = Pine
                )
                MetricChart(
                    title = "Waist Trend (cm)",
                    data = metrics.mapNotNull { it.waistCircumferenceCm },
                    color = Turmeric
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StreakCard(streak = streak)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MetricChart(
                title = "Weight Trend (kg)",
                data = metrics.mapNotNull { it.weightKg },
                color = Pine
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MetricChart(
                title = "Waist Trend (cm)",
                data = metrics.mapNotNull { it.waistCircumferenceCm },
                color = Turmeric
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LogMetricsCard(
                weightInput = weightInput,
                waistInput = waistInput,
                onWeightChange = { weightInput = it },
                onWaistChange = { waistInput = it },
                onLog = { weight, waist ->
                    viewModel.logMetrics(waist, weight)
                    weightInput = ""
                    waistInput = ""
                }
            )
        }
    }
}

@Composable
fun LogMetricsCard(
    weightInput: String,
    waistInput: String,
    onWeightChange: (String) -> Unit,
    onWaistChange: (String) -> Unit,
    onLog: (Double?, Double?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Log Today's Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = onWeightChange,
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = waistInput,
                    onValueChange = onWaistChange,
                    label = { Text("Waist (cm)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    val weight = weightInput.toDoubleOrNull()
                    val waist = waistInput.toDoubleOrNull()
                    if (weight != null || waist != null) {
                        onLog(weight, waist)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Pine)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Measurements")
            }
        }
    }
}

@Composable
fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Turmeric),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Pine
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$streak DAY STREAK",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Pine
                    )
                }
                Text(
                    text = "Keep it up! You're doing great.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Pine.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun MetricChart(title: String, data: List<Double>, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (data.size < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Need at least 2 entries for trend",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            } else {
                Sparkline(
                    data = data,
                    color = color,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
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
        
        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1)
        
        val points = data.mapIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - min) / range * height).toFloat()
            Offset(x, y)
        }
        
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
