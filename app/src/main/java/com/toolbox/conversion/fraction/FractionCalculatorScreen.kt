package com.toolbox.conversion.fraction

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
fun FractionCalculatorScreen() {
    val context = LocalContext.current

    var w1 by rememberSaveable { mutableStateOf("") }
    var n1 by rememberSaveable { mutableStateOf("1") }
    var d1 by rememberSaveable { mutableStateOf("2") }

    var op by rememberSaveable { mutableStateOf("+") }

    var w2 by rememberSaveable { mutableStateOf("") }
    var n2 by rememberSaveable { mutableStateOf("1") }
    var d2 by rememberSaveable { mutableStateOf("4") }

    var singleW by rememberSaveable { mutableStateOf("") }
    var singleN by rememberSaveable { mutableStateOf("6") }
    var singleD by rememberSaveable { mutableStateOf("8") }

    var tabIndex by rememberSaveable { mutableStateOf(0) }
    var decimalInput by rememberSaveable { mutableStateOf("0.75") }

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
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 },
                label = { Text("Operations") },
            )
            FilterChip(
                selected = tabIndex == 1,
                onClick = { tabIndex = 1 },
                label = { Text("Simplify") },
            )
            FilterChip(
                selected = tabIndex == 2,
                onClick = { tabIndex = 2 },
                label = { Text("Dec ↔ Frac") },
            )
        }

        when (tabIndex) {
            0 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Fraction 1", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        FractionInput(whole = w1, onWholeChange = { w1 = it }, num = n1, onNumChange = { n1 = it }, den = d1, onDenChange = { d1 = it })

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            listOf("+", "−", "×", "÷").forEach { operator ->
                                FilterChip(
                                    selected = op == operator,
                                    onClick = { op = operator },
                                    label = { Text(operator, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                                )
                            }
                        }

                        Text("Fraction 2", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        FractionInput(whole = w2, onWholeChange = { w2 = it }, num = n2, onNumChange = { n2 = it }, den = d2, onDenChange = { d2 = it })
                    }
                }

                val res = computeFractionResult(w1, n1, d1, op, w2, n2, d2)

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
                        Text("Result", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(res.first, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        if (res.second.isNotEmpty()) {
                            Text(res.second, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        OutlinedButton(
                            onClick = { copyToClipboard(context, "Fraction Result", res.first) },
                        ) { Text("Copy result") }
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
                        Text("Enter Fraction to Simplify", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        FractionInput(whole = singleW, onWholeChange = { singleW = it }, num = singleN, onNumChange = { singleN = it }, den = singleD, onDenChange = { singleD = it })
                    }
                }

                val simplified = simplifyFractionResult(singleW, singleN, singleD)

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
                        Text("Simplified", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(simplified.first, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        if (simplified.second.isNotEmpty()) {
                            Text(simplified.second, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
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
                        Text("Decimal Number", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = decimalInput,
                            onValueChange = { decimalInput = it },
                            label = { Text("e.g. 0.75, 2.375") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        )
                    }
                }

                val converted = decimalToFraction(decimalInput)
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
                        Text("Equivalent Fraction", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(converted, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        OutlinedButton(onClick = { copyToClipboard(context, "Fraction", converted) }) {
                            Text("Copy fraction")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FractionInput(
    whole: String,
    onWholeChange: (String) -> Unit,
    num: String,
    onNumChange: (String) -> Unit,
    den: String,
    onDenChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = whole,
            onValueChange = onWholeChange,
            label = { Text("Whole") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Column(
            modifier = Modifier.weight(1.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = num,
                onValueChange = onNumChange,
                label = { Text("Numerator") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = den,
                onValueChange = onDenChange,
                label = { Text("Denominator") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }
}

private fun gcd(a: Long, b: Long): Long {
    var x = abs(a)
    var y = abs(b)
    while (y != 0L) {
        val t = y
        y = x % y
        x = t
    }
    return if (x == 0L) 1L else x
}

fun toImproper(wholeStr: String, numStr: String, denStr: String): Pair<Long, Long>? {
    val den = denStr.toLongOrNull() ?: return null
    if (den == 0L) return null
    val num = numStr.toLongOrNull() ?: 0L
    val whole = wholeStr.toLongOrNull() ?: 0L
    val sign = if (whole < 0 || num < 0 || den < 0) -1L else 1L
    val totalNum = abs(whole) * abs(den) + abs(num)
    return (sign * totalNum) to abs(den)
}

fun computeFractionResult(w1: String, n1: String, d1: String, op: String, w2: String, n2: String, d2: String): Pair<String, String> {
    val f1 = toImproper(w1, n1, d1) ?: return "Invalid Fraction 1" to ""
    val f2 = toImproper(w2, n2, d2) ?: return "Invalid Fraction 2" to ""

    val num1 = f1.first; val den1 = f1.second
    val num2 = f2.first; val den2 = f2.second

    var resNum: Long
    var resDen: Long

    when (op) {
        "+" -> {
            resNum = num1 * den2 + num2 * den1
            resDen = den1 * den2
        }
        "−" -> {
            resNum = num1 * den2 - num2 * den1
            resDen = den1 * den2
        }
        "×" -> {
            resNum = num1 * num2
            resDen = den1 * den2
        }
        "÷" -> {
            if (num2 == 0L) return "Cannot divide by 0" to ""
            resNum = num1 * den2
            resDen = den1 * num2
            if (resDen < 0) {
                resNum = -resNum
                resDen = -resDen
            }
        }
        else -> return "Unknown Operator" to ""
    }

    val div = gcd(resNum, resDen)
    val simpNum = resNum / div
    val simpDen = resDen / div

    if (simpDen == 1L) {
        val dec = simpNum.toDouble()
        return "$simpNum" to "Decimal: $dec"
    }

    val dec = (simpNum.toDouble() / simpDen.toDouble())
    val decStr = String.format("%.4f", dec)

    if (abs(simpNum) > simpDen) {
        val wholePart = simpNum / simpDen
        val remNum = abs(simpNum % simpDen)
        return "$simpNum / $simpDen" to "Mixed: $wholePart $remNum/$simpDen  (≈ $decStr)"
    }

    return "$simpNum / $simpDen" to "Decimal: ≈ $decStr"
}

fun simplifyFractionResult(w: String, n: String, d: String): Pair<String, String> {
    val f = toImproper(w, n, d) ?: return "Invalid Fraction" to ""
    val div = gcd(f.first, f.second)
    val num = f.first / div
    val den = f.second / div
    if (den == 1L) return "$num" to ""
    if (abs(num) > den) {
        val whole = num / den
        val rem = abs(num % den)
        return "$num / $den" to "Mixed: $whole $rem/$den"
    }
    return "$num / $den" to ""
}

fun decimalToFraction(decStr: String): String {
    val d = decStr.toDoubleOrNull() ?: return "Invalid decimal"
    val isNeg = d < 0
    val absD = abs(d)
    val whole = absD.toLong()
    val frac = absD - whole

    if (frac < 1e-9) return if (isNeg) "-$whole" else "$whole"

    val precision = 1000000L
    val num = (frac * precision).toLong()
    val div = gcd(num, precision)
    val simpNum = num / div
    val simpDen = precision / div

    val finalNum = whole * simpDen + simpNum
    val prefix = if (isNeg) "-" else ""
    return if (whole > 0) {
        "$prefix$finalNum / $simpDen  ($prefix$whole $simpNum/$simpDen)"
    } else {
        "$prefix$simpNum / $simpDen"
    }
}
