package com.toolbox.conversion.ratio

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.toolbox.core.ui.copyToClipboard
import kotlin.math.abs

@Composable
fun RatioCalculatorScreen() {
    val context = LocalContext.current
    var selectedMode by rememberSaveable { mutableStateOf(0) }

    var aStr by rememberSaveable { mutableStateOf("2") }
    var bStr by rememberSaveable { mutableStateOf("5") }
    var cStr by rememberSaveable { mutableStateOf("10") }
    var dStr by rememberSaveable { mutableStateOf("") }

    var simA by rememberSaveable { mutableStateOf("1920") }
    var simB by rememberSaveable { mutableStateOf("1080") }

    var origA by rememberSaveable { mutableStateOf("3") }
    var origB by rememberSaveable { mutableStateOf("4") }
    var totalAmount by rememberSaveable { mutableStateOf("350") }

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
                selected = selectedMode == 0,
                onClick = { selectedMode = 0 },
                label = { Text("Solve Proportion") },
            )
            FilterChip(
                selected = selectedMode == 1,
                onClick = { selectedMode = 1 },
                label = { Text("Simplify Ratio") },
            )
            FilterChip(
                selected = selectedMode == 2,
                onClick = { selectedMode = 2 },
                label = { Text("Divide / Scale") },
            )
        }

        when (selectedMode) {
            0 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Proportion Solver: A : B = C : D",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            "Leave exactly one value blank to calculate it.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = aStr,
                                onValueChange = { aStr = it },
                                label = { Text("A") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                            Text(":", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = bStr,
                                onValueChange = { bStr = it },
                                label = { Text("B") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                            Text("=", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = cStr,
                                onValueChange = { cStr = it },
                                label = { Text("C") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                            Text(":", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = dStr,
                                onValueChange = { dStr = it },
                                label = { Text("D") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                        }
                    }
                }

                val solved = solveProportion(aStr, bStr, cStr, dStr)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Proportion Result", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(solved.first, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        if (solved.second.isNotEmpty()) {
                            Text(solved.second, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        OutlinedButton(onClick = { copyToClipboard(context, "Proportion", solved.first) }) {
                            Text("Copy result")
                        }
                    }
                }
            }

            1 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Simplify Ratio: A : B", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = simA,
                                onValueChange = { simA = it },
                                label = { Text("A") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                            Text(":", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = simB,
                                onValueChange = { simB = it },
                                label = { Text("B") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                        }
                    }
                }

                val simplified = simplifyRatio(simA, simB)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Simplified Ratio", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(simplified.first, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        if (simplified.second.isNotEmpty()) {
                            Text(simplified.second, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        OutlinedButton(onClick = { copyToClipboard(context, "Ratio", simplified.first) }) {
                            Text("Copy result")
                        }
                    }
                }
            }

            2 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Divide Total by Ratio A : B", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = origA,
                                onValueChange = { origA = it },
                                label = { Text("Part A") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                            Text(":", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = origB,
                                onValueChange = { origB = it },
                                label = { Text("Part B") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                        }
                        OutlinedTextField(
                            value = totalAmount,
                            onValueChange = { totalAmount = it },
                            label = { Text("Total Quantity to Divide") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        )
                    }
                }

                val division = divideRatio(origA, origB, totalAmount)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Division Result", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(division.first, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        if (division.second.isNotEmpty()) {
                            Text(division.second, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        OutlinedButton(onClick = { copyToClipboard(context, "Ratio Division", division.first) }) {
                            Text("Copy result")
                        }
                    }
                }
            }
        }
    }
}

fun solveProportion(aStr: String, bStr: String, cStr: String, dStr: String): Pair<String, String> {
    val a = aStr.toDoubleOrNull()
    val b = bStr.toDoubleOrNull()
    val c = cStr.toDoubleOrNull()
    val d = dStr.toDoubleOrNull()

    val filled = listOf(a != null, b != null, c != null, d != null).count { it }
    if (filled != 3) {
        return "Fill exactly 3 values" to "Leave exactly one field blank"
    }

    return try {
        if (a == null) {
            val ans = (b!! * c!!) / d!!
            "A = $ans" to "Formula: (B × C) / D"
        } else if (b == null) {
            val ans = (a * d!!) / c!!
            "B = $ans" to "Formula: (A × D) / C"
        } else if (c == null) {
            val ans = (a * d!!) / b
            "C = $ans" to "Formula: (A × D) / B"
        } else {
            val ans = (b * c) / a
            "D = $ans" to "Formula: (B × C) / A"
        }
    } catch (_: Exception) {
        "Math Error" to "Division by zero"
    }
}

fun simplifyRatio(aStr: String, bStr: String): Pair<String, String> {
    val a = aStr.toDoubleOrNull() ?: return "Invalid A" to ""
    val b = bStr.toDoubleOrNull() ?: return "Invalid B" to ""

    if (a == 0.0 || b == 0.0) return "Cannot simplify with 0" to ""

    var mult = 1L
    while ((a * mult) % 1.0 != 0.0 || (b * mult) % 1.0 != 0.0) {
        mult *= 10
        if (mult > 1_000_000) break
    }

    val intA = (a * mult).toLong()
    val intB = (b * mult).toLong()

    fun gcd(x: Long, y: Long): Long {
        var x1 = abs(x)
        var y1 = abs(y)
        while (y1 != 0L) {
            val t = y1
            y1 = x1 % y1
            x1 = t
        }
        return if (x1 == 0L) 1L else x1
    }

    val g = gcd(intA, intB)
    val simA = intA / g
    val simB = intB / g

    val decimalVal = a / b
    val decStr = String.format("%.4f", decimalVal)

    return "$simA : $simB" to "Decimal factor: $decStr  (or 1 : ${String.format("%.4f", b / a)})"
}

fun divideRatio(aStr: String, bStr: String, totalStr: String): Pair<String, String> {
    val a = aStr.toDoubleOrNull() ?: return "Invalid A" to ""
    val b = bStr.toDoubleOrNull() ?: return "Invalid B" to ""
    val total = totalStr.toDoubleOrNull() ?: return "Invalid Total" to ""

    val sum = a + b
    if (sum == 0.0) return "Sum cannot be 0" to ""

    val partA = (a / sum) * total
    val partB = (b / sum) * total

    val partAStr = String.format("%.2f", partA)
    val partBStr = String.format("%.2f", partB)

    return "A = $partAStr  |  B = $partBStr" to "Percentages: ${(a/sum*100).toInt()}% and ${(b/sum*100).toInt()}%"
}
