package com.toolbox.healthfitness

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CalorieBurnCalcScreen() {
    var weightKg by remember { mutableStateOf("70") }
    var durationMins by remember { mutableStateOf("45") }
    var activity by remember { mutableStateOf("Running") }
    var burnedCalories by remember { mutableStateOf<Double?>(null) }

    // MET values: Metabolic Equivalent of Task
    val metMap = mapOf(
        "Walking (Moderate)" to 3.5,
        "Running (8 km/h)" to 8.3,
        "Cycling (Moderate)" to 6.8,
        "Swimming" to 7.0,
        "Jump Rope" to 10.0,
        "Yoga / Stretching" to 2.5
    )

    fun calculate() {
        val w = weightKg.toDoubleOrNull() ?: 70.0
        val d = durationMins.toDoubleOrNull() ?: 45.0
        val met = metMap[activity] ?: 8.3
        // Calories = MET * weight (kg) * time (hours)
        val cal = met * w * (d / 60.0)
        burnedCalories = cal
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("MET Calorie Expenditure Estimator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Scientifically compute energy expenditure based on body mass and standard exercise MET ratings.")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = weightKg,
                onValueChange = { weightKg = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = durationMins,
                onValueChange = { durationMins = it },
                label = { Text("Duration (mins)") },
                modifier = Modifier.weight(1f)
            )
        }

        Text("Select Activity:", fontWeight = FontWeight.SemiBold)
        metMap.keys.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { act ->
                    FilterChip(
                        selected = activity == act,
                        onClick = { activity = act },
                        label = { Text(act, style = MaterialTheme.typography.bodySmall) }
                    )
                }
            }
        }

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Burned Calories")
        }

        burnedCalories?.let { cal ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estimated Energy Burned", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${cal.toInt()} kcal",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Calculated via MET index (${metMap[activity] ?: 8.3}) for $durationMins mins of $activity.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
