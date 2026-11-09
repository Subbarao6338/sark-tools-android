package com.toolbox.healthfitness

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun WaterIntakeCalcScreen() {
    var weightInput by remember { mutableStateOf("70") }
    var isMetric by remember { mutableStateOf(true) }
    var activityLevel by remember { mutableStateOf(1) }
    var climate by remember { mutableStateOf(1) }

    val weight = weightInput.toDoubleOrNull() ?: 0.0
    val weightInKg = if (isMetric) weight else weight * 0.45359237

    val recommendedMl = if (weightInKg > 0) {
        (weightInKg * 35.0) + (activityLevel * 350.0) + (climate * 250.0)
    } else 0.0

    val recommendedOz = recommendedMl * 0.033814
    val standardGlasses = if (recommendedMl > 0) (recommendedMl / 250.0) else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDrink,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format(Locale.US, "%.1f L / day", recommendedMl / 1000.0),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = String.format(Locale.US, "≈ %.0f ml (%.0f fl oz) • %.1f glasses (250ml)", recommendedMl, recommendedOz, standardGlasses),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedTextField(
            value = weightInput,
            onValueChange = { weightInput = it },
            label = { Text(if (isMetric) "Body Weight (kg)" else "Body Weight (lbs)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = isMetric,
                onClick = { isMetric = true },
                label = { Text("Metric (kg)") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isMetric,
                onClick = { isMetric = false },
                label = { Text("Imperial (lbs)") },
                modifier = Modifier.weight(1f)
            )
        }

        Text("Daily Physical Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Sedentary", "Moderate", "Active", "Intense").forEachIndexed { index, label ->
                FilterChip(
                    selected = activityLevel == index,
                    onClick = { activityLevel = index },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text("Environment / Climate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Cool", "Normal", "Hot/Humid").forEachIndexed { index, label ->
                FilterChip(
                    selected = climate == index,
                    onClick = { climate = index },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Suggested Hydration Schedule", fontWeight = FontWeight.Bold)
                }
                val glassAmount = if (recommendedMl > 0) recommendedMl / 8.0 else 250.0
                listOf(
                    "07:00 AM" to "Wake up & rehydrate (1 glass)",
                    "09:30 AM" to "Mid-morning focus glass",
                    "12:30 PM" to "30 mins before lunch",
                    "02:30 PM" to "Afternoon refreshment",
                    "04:30 PM" to "Pre-workout / study hydration",
                    "06:30 PM" to "Early evening hydration",
                    "08:00 PM" to "Dinner time sip",
                    "09:30 PM" to "Final glass before sleep"
                ).forEach { (time, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(time, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                        Text("$desc (~${glassAmount.toInt()}ml)", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
