package com.toolbox.conversion.fuel

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FuelCalculatorScreen() {
    var distanceInput by remember { mutableStateOf("250") }
    var efficiencyInput by remember { mutableStateOf("15") }
    var priceInput by remember { mutableStateOf("105") }
    var unitMode by remember { mutableIntStateOf(0) } // 0: Metric (km, km/L), 1: US (miles, MPG)

    val distance = distanceInput.toDoubleOrNull() ?: 0.0
    val efficiency = efficiencyInput.toDoubleOrNull() ?: 1.0
    val price = priceInput.toDoubleOrNull() ?: 0.0

    val fuelNeeded = if (efficiency > 0) distance / efficiency else 0.0
    val totalCost = fuelNeeded * price
    val costPerKm = if (distance > 0) totalCost / distance else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Unit system selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = unitMode == 0,
                onClick = {
                    unitMode = 0
                    if (efficiencyInput == "35") efficiencyInput = "15"
                },
                label = { Text("Metric (km, Liters)") },
                shape = RoundedCornerShape(20.dp),
            )
            FilterChip(
                selected = unitMode == 1,
                onClick = {
                    unitMode = 1
                    if (efficiencyInput == "15") efficiencyInput = "35"
                },
                label = { Text("Imperial (Miles, Gallons)") },
                shape = RoundedCornerShape(20.dp),
            )
        }

        // Hero Cost Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalGasStation,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Total Trip Cost",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                    Text(
                        text = String.format("%.2f", totalCost),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "${String.format("%.2f", fuelNeeded)} ${if (unitMode == 0) "Liters" else "Gallons"} required",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        // Inputs
        InputCard(
            label = if (unitMode == 0) "Trip Distance (km)" else "Trip Distance (miles)",
            value = distanceInput,
            onValueChange = { distanceInput = it },
        )

        InputCard(
            label = if (unitMode == 0) "Fuel Economy (km/L)" else "Fuel Economy (MPG)",
            value = efficiencyInput,
            onValueChange = { efficiencyInput = it },
        )

        InputCard(
            label = if (unitMode == 0) "Fuel Price per Liter" else "Fuel Price per Gallon",
            value = priceInput,
            onValueChange = { priceInput = it },
        )

        // Trip Efficiency Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Trip Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Cost per Distance Unit", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${String.format("%.2f", costPerKm)} / ${if (unitMode == 0) "km" else "mi"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Fuel Consumption", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (unitMode == 0) {
                            val lPer100 = if (efficiency > 0) 100.0 / efficiency else 0.0
                            String.format("%.1f L / 100km", lPer100)
                        } else {
                            String.format("%.1f MPG", efficiency)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun InputCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(12.dp),
            )
        }
    }
}
