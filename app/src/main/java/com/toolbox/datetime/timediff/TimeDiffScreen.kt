package com.toolbox.datetime.timediff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class CityZone(val name: String, val zoneId: ZoneId, val country: String)

val commonZones = listOf(
    CityZone("London", ZoneId.of("Europe/London"), "UK (GMT/BST)"),
    CityZone("New York", ZoneId.of("America/New_York"), "USA (EST/EDT)"),
    CityZone("San Francisco", ZoneId.of("America/Los_Angeles"), "USA (PST/PDT)"),
    CityZone("Tokyo", ZoneId.of("Asia/Tokyo"), "Japan (JST)"),
    CityZone("Sydney", ZoneId.of("Australia/Sydney"), "Australia (AEST/AEDT)"),
    CityZone("Dubai", ZoneId.of("Asia/Dubai"), "UAE (GST)"),
    CityZone("New Delhi", ZoneId.of("Asia/Kolkata"), "India (IST)"),
    CityZone("Singapore", ZoneId.of("Asia/Singapore"), "Singapore (SGT)"),
    CityZone("Berlin", ZoneId.of("Europe/Berlin"), "Germany (CET/CEST)"),
)

@Composable
fun TimeDiffScreen() {
    val selectedCities = remember {
        mutableStateListOf(
            commonZones[6], // New Delhi
            commonZones[0], // London
            commonZones[1], // New York
        )
    }

    var baseHourOffset by remember { mutableFloatStateOf(0f) }

    val baseInstant = remember { Instant.now() }
    val simulatedInstant = baseInstant.plusSeconds((baseHourOffset * 3600).toLong())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Meeting Slider Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Meeting Time Slider",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = if (baseHourOffset >= 0) "+${baseHourOffset.toInt()} hrs" else "${baseHourOffset.toInt()} hrs",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = baseHourOffset,
                    onValueChange = { baseHourOffset = it },
                    valueRange = -12f..12f,
                    steps = 23,
                )
                Text(
                    text = "Slide forward or backward to test meeting times across timezones. Green badge indicates comfortable working hours (9 AM - 6 PM).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                )
            }
        }

        Text(
            text = "Active Time Zones (${selectedCities.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        selectedCities.forEach { city ->
            val cityTime = simulatedInstant.atZone(city.zoneId)
            val hour = cityTime.hour
            val isWorkHours = hour in 9..18
            val timeString = cityTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
            val dateString = cityTime.format(DateTimeFormatter.ofPattern("EEE, MMM d"))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isWorkHours) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isWorkHours) Color(0xFF2E7D32).copy(alpha = 0.2f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            ) {
                                Text(
                                    text = if (isWorkHours) "Working Hours" else "Off Hours",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isWorkHours) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${city.country} • $dateString",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    if (selectedCities.size > 1) {
                        IconButton(
                            onClick = { selectedCities.remove(city) },
                            modifier = Modifier.padding(start = 8.dp),
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        // Add more cities chips
        val availableToAdd = commonZones.filter { it !in selectedCities }
        if (availableToAdd.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Add Cities to Comparison", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                availableToAdd.take(3).forEach { city ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).clickable { selectedCities.add(city) },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(city.name, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
