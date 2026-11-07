package com.toolbox.conversion.units

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

enum class TempScale(val label: String, val symbol: String) {
    Celsius("Celsius", "°C"),
    Fahrenheit("Fahrenheit", "°F"),
    Kelvin("Kelvin", "K"),
    Rankine("Rankine", "°R"),
}

object TemperatureConverterEngine {
    fun toCelsius(value: Double, scale: TempScale): Double = when (scale) {
        TempScale.Celsius -> value
        TempScale.Fahrenheit -> (value - 32.0) * 5.0 / 9.0
        TempScale.Kelvin -> value - 273.15
        TempScale.Rankine -> (value - 491.67) * 5.0 / 9.0
    }

    fun fromCelsius(celsius: Double, target: TempScale): Double = when (target) {
        TempScale.Celsius -> celsius
        TempScale.Fahrenheit -> (celsius * 9.0 / 5.0) + 32.0
        TempScale.Kelvin -> celsius + 273.15
        TempScale.Rankine -> (celsius + 273.15) * 9.0 / 5.0
    }

    fun convert(value: Double, from: TempScale, to: TempScale): Double {
        val c = toCelsius(value, from)
        return fromCelsius(c, to)
    }

    fun format(value: Double): String = String.format("%.2f", value).trimEnd('0').trimEnd('.')
}

@Composable
fun TemperatureConverterScreen() {
    var inputValue by remember { mutableStateOf("25") }
    var selectedScale by remember { mutableStateOf(TempScale.Celsius) }

    val numericInput = inputValue.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Convert From: ${selectedScale.label} (${selectedScale.symbol})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Text("All Scales Live Readout", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

        TempScale.entries.forEach { scale ->
            val isSelected = scale == selectedScale
            val converted = TemperatureConverterEngine.convert(numericInput, selectedScale, scale)
            val formatted = TemperatureConverterEngine.format(converted)

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedScale = scale },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(scale.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(scale.symbol, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        "$formatted ${scale.symbol}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // Reference card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Physical Reference Benchmarks", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text("• Absolute Zero: -273.15 °C | -459.67 °F | 0 K", style = MaterialTheme.typography.bodySmall)
                Text("• Water Freezing: 0 °C | 32 °F | 273.15 K", style = MaterialTheme.typography.bodySmall)
                Text("• Human Body: 37 °C | 98.6 °F | 310.15 K", style = MaterialTheme.typography.bodySmall)
                Text("• Water Boiling: 100 °C | 212 °F | 373.15 K", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
