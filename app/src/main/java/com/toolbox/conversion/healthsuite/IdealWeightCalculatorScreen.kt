package com.toolbox.conversion.healthsuite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun IdealWeightCalculatorScreen() {
    var gender by rememberSaveable { mutableStateOf("Male") }
    var heightCmStr by rememberSaveable { mutableStateOf("175") }

    val heightCm = heightCmStr.toDoubleOrNull() ?: 0.0
    val heightInches = heightCm / 2.54
    val inchesOver5Feet = (heightInches - 60.0).coerceAtLeast(0.0)

    val isMale = gender == "Male"

    val devineKg = if (isMale) 50.0 + 2.3 * inchesOver5Feet else 45.5 + 2.3 * inchesOver5Feet
    val robinsonKg = if (isMale) 52.0 + 1.9 * inchesOver5Feet else 49.0 + 1.7 * inchesOver5Feet
    val millerKg = if (isMale) 56.2 + 1.41 * inchesOver5Feet else 53.1 + 1.36 * inchesOver5Feet
    val hamwiKg = if (isMale) 48.0 + 2.7 * inchesOver5Feet else 45.5 + 2.2 * inchesOver5Feet

    val heightM = heightCm / 100.0
    val minBmiKg = 18.5 * heightM * heightM
    val maxBmiKg = 24.9 * heightM * heightM

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Your Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = gender == "Male",
                        onClick = { gender = "Male" },
                        label = { Text("Male") },
                    )
                    FilterChip(
                        selected = gender == "Female",
                        onClick = { gender = "Female" },
                        label = { Text("Female") },
                    )
                }

                OutlinedTextField(
                    value = heightCmStr,
                    onValueChange = { heightCmStr = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }
        }

        if (heightCm > 50.0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Average Ideal Body Weight", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    val avgKg = (devineKg + robinsonKg + millerKg + hamwiKg) / 4.0
                    Text(
                        String.format("%.1f kg (%.1f lbs)", avgKg, avgKg * 2.20462),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Healthy BMI Weight Range:\n${String.format("%.1f", minBmiKg)} kg – ${String.format("%.1f", maxBmiKg)} kg",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Medical Formulas Comparison", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    FormulaRow("Devine Formula (1974)", devineKg)
                    FormulaRow("Robinson Formula (1983)", robinsonKg)
                    FormulaRow("Miller Formula (1983)", millerKg)
                    FormulaRow("Hamwi Formula (1964)", hamwiKg)
                }
            }
        }
    }
}

@Composable
private fun FormulaRow(name: String, kg: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium)
        Text(String.format("%.1f kg", kg), fontWeight = FontWeight.Bold)
    }
}
