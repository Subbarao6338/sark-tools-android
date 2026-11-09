package com.toolbox.healthfitness

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun PaceCalcScreen() {
    var distanceKmInput by remember { mutableStateOf("10.0") }
    var hoursInput by remember { mutableStateOf("0") }
    var minsInput by remember { mutableStateOf("50") }
    var secsInput by remember { mutableStateOf("0") }

    val dist = distanceKmInput.toDoubleOrNull() ?: 10.0
    val h = hoursInput.toIntOrNull() ?: 0
    val m = minsInput.toIntOrNull() ?: 0
    val s = secsInput.toIntOrNull() ?: 0

    val totalSeconds = (h * 3600) + (m * 60) + s
    val speedKmh = if (totalSeconds > 0 && dist > 0) (dist / (totalSeconds / 3600.0)) else 0.0

    val paceSecsPerKm = if (dist > 0 && totalSeconds > 0) (totalSeconds / dist) else 0.0
    val paceKmMin = (paceSecsPerKm / 60).toInt()
    val paceKmSec = (paceSecsPerKm % 60).toInt()

    val paceSecsPerMile = paceSecsPerKm * 1.60934
    val paceMileMin = (paceSecsPerMile / 60).toInt()
    val paceMileSec = (paceSecsPerMile % 60).toInt()

    fun formatDuration(totalSec: Double): String {
        val hrs = (totalSec / 3600).toInt()
        val mins = ((totalSec % 3600) / 60).toInt()
        val secs = (totalSec % 60).toInt()
        return if (hrs > 0) String.format(Locale.US, "%d:%02d:%02d", hrs, mins, secs)
        else String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    val predictions = listOf(
        "5 km" to (paceSecsPerKm * 5.0),
        "10 km" to (paceSecsPerKm * 10.0),
        "Half Marathon (21.1 km)" to (paceSecsPerKm * 21.0975),
        "Marathon (42.2 km)" to (paceSecsPerKm * 42.195)
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
                    imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format(Locale.US, "%d:%02d /km", paceKmMin, paceKmSec),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = String.format(Locale.US, "%d:%02d /mi • %.2f km/h", paceMileMin, paceMileSec, speedKmh),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedTextField(
            value = distanceKmInput,
            onValueChange = { distanceKmInput = it },
            label = { Text("Distance (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = hoursInput,
                onValueChange = { hoursInput = it },
                label = { Text("Hours") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = minsInput,
                onValueChange = { minsInput = it },
                label = { Text("Minutes") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = secsInput,
                onValueChange = { secsInput = it },
                label = { Text("Seconds") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Text("Race Finish Time Predictor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        predictions.forEach { (event, finishSecs) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(event, fontWeight = FontWeight.Medium)
                    Text(
                        if (paceSecsPerKm > 0) formatDuration(finishSecs) else "--:--",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
