package com.toolbox.healthfitness

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun TargetHeartRateScreen() {
    var ageInput by remember { mutableStateOf("28") }
    var restingHrInput by remember { mutableStateOf("65") }

    val age = ageInput.toIntOrNull() ?: 28
    val restingHr = restingHrInput.toIntOrNull() ?: 65

    val maxHr = (208.0 - (0.7 * age)).toInt()
    val hrr = maxOf(0, maxHr - restingHr)

    data class Zone(val name: String, val pctRange: String, val minPct: Double, val maxPct: Double, val benefit: String)
    val zones = listOf(
        Zone("Zone 1: Very Light", "50% - 60%", 0.50, 0.60, "Active recovery, warmup & cooldown"),
        Zone("Zone 2: Light / Fat Burn", "60% - 70%", 0.60, 0.70, "Aerobic endurance & base fitness"),
        Zone("Zone 3: Moderate / Aerobic", "70% - 80%", 0.70, 0.80, "Cardiovascular capacity improvement"),
        Zone("Zone 4: Hard / Anaerobic", "80% - 90%", 0.80, 0.90, "Speed endurance & lactate threshold"),
        Zone("Zone 5: Maximum", "90% - 100%", 0.90, 1.00, "Peak power, sprint intervals & VO2 Max")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$maxHr BPM",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Estimated Maximum Heart Rate (Tanaka formula)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = ageInput,
                onValueChange = { ageInput = it },
                label = { Text("Age (years)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = restingHrInput,
                onValueChange = { restingHrInput = it },
                label = { Text("Resting HR (BPM)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Text("Karvonen Training Zones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        zones.forEach { zone ->
            val minTarget = (restingHr + (hrr * zone.minPct)).toInt()
            val maxTarget = (restingHr + (hrr * zone.maxPct)).toInt()
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(zone.name, fontWeight = FontWeight.Bold)
                        Text("$minTarget - $maxTarget BPM", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Text("${zone.pctRange} HRR • ${zone.benefit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
