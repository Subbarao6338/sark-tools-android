package com.toolbox.conversion.healthsuite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SleepCycleScreen() {
    var mode by rememberSaveable { mutableIntStateOf(0) }
    var targetHourStr by rememberSaveable { mutableStateOf("07") }
    var targetMinuteStr by rememberSaveable { mutableStateOf("00") }

    val dtf = DateTimeFormatter.ofPattern("hh:mm a")

    val times = if (mode == 0) {
        val now = LocalTime.now().plusMinutes(14)
        listOf(
            now.plusMinutes(90) to (1 to "1.5 hours (1 cycle)"),
            now.plusMinutes(180) to (2 to "3 hours (2 cycles)"),
            now.plusMinutes(270) to (3 to "4.5 hours (3 cycles)"),
            now.plusMinutes(360) to (4 to "6 hours (4 cycles) - Good"),
            now.plusMinutes(450) to (5 to "7.5 hours (5 cycles) - Optimal"),
            now.plusMinutes(540) to (6 to "9 hours (6 cycles) - Well Rested"),
        )
    } else {
        val target = try {
            LocalTime.of(targetHourStr.toInt(), targetMinuteStr.toInt())
        } catch (_: Exception) {
            LocalTime.of(7, 0)
        }
        val targetWithLatency = target.minusMinutes(14)
        listOf(
            targetWithLatency.minusMinutes(540) to (6 to "9 hours (6 cycles) - Well Rested"),
            targetWithLatency.minusMinutes(450) to (5 to "7.5 hours (5 cycles) - Optimal"),
            targetWithLatency.minusMinutes(360) to (4 to "6 hours (4 cycles) - Good"),
            targetWithLatency.minusMinutes(270) to (3 to "4.5 hours (3 cycles)"),
            targetWithLatency.minusMinutes(180) to (2 to "3 hours (2 cycles)"),
            targetWithLatency.minusMinutes(90) to (1 to "1.5 hours (1 cycle)"),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = mode == 0,
                onClick = { mode = 0 },
                label = { Text("Sleep Now") },
            )
            FilterChip(
                selected = mode == 1,
                onClick = { mode = 1 },
                label = { Text("Set Wake Up Time") },
            )
        }

        if (mode == 1) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Target Wake-Up Time", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = targetHourStr,
                            onValueChange = { targetHourStr = it.filter { c -> c.isDigit() }.take(2) },
                            label = { Text("Hour (0-23)") },
                            modifier = Modifier.weight(1f),
                        )
                        Text(":", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = targetMinuteStr,
                            onValueChange = { targetMinuteStr = it.filter { c -> c.isDigit() }.take(2) },
                            label = { Text("Minute (0-59)") },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (mode == 0) "Optimal Wake Up Times (assuming 14m to fall asleep):" else "Recommended Bedtimes (assuming 14m to fall asleep):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )

                times.forEach { (time, meta) ->
                    val isBest = meta.first in 4..5
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isBest) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = time.format(dtf),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isBest) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = meta.second,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal,
                                color = if (isBest) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
