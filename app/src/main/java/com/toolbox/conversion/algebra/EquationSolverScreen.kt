package com.toolbox.conversion.algebra

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.components.TileSelector
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun EquationSolverScreen() {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val equationTypes = listOf("Linear (ax+b=c)", "Quadratic (ax²+bx+c=0)", "2x2 Linear System", "Cubic (ax³+bx²+cx+d=0)")

    Column(modifier = Modifier.fillMaxSize()) {
        TileSelector(
            options = equationTypes,
            selectedIndex = selectedIndex,
            onSelectIndex = { selectedIndex = it },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (selectedIndex) {
                0 -> LinearEquationSolver()
                1 -> QuadraticEquationSolver()
                2 -> System2x2Solver()
                3 -> CubicEquationSolver()
            }
        }
    }
}

@Composable
private fun LinearEquationSolver() {
    var aStr by rememberSaveable { mutableStateOf("2") }
    var bStr by rememberSaveable { mutableStateOf("3") }
    var cStr by rememberSaveable { mutableStateOf("11") }

    val a = aStr.toDoubleOrNull()
    val b = bStr.toDoubleOrNull()
    val c = cStr.toDoubleOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Linear Equation: a·x + b = c", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = aStr,
                    onValueChange = { aStr = it },
                    label = { Text("a") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = bStr,
                    onValueChange = { bStr = it },
                    label = { Text("b") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = cStr,
                    onValueChange = { cStr = it },
                    label = { Text("c") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Solution & Step-by-Step", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            if (a == null || b == null || c == null) {
                Text("Enter valid real numbers for a, b, and c.", color = MaterialTheme.colorScheme.error)
            } else if (a == 0.0) {
                if (b == c) {
                    Text("Infinite Solutions: 0x + $b = $c is true for all x ∈ ℝ.")
                } else {
                    Text("No Solution: 0x + $b = $c is a contradiction ($b ≠ $c).", color = MaterialTheme.colorScheme.error)
                }
            } else {
                val x = (c - b) / a
                Text("Step 1: Subtract b ($b) from both sides:", style = MaterialTheme.typography.bodyMedium)
                Text("${fmt(a)}x = ${fmt(c)} - ${fmt(b)} = ${fmt(c - b)}", fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(4.dp))
                Text("Step 2: Divide both sides by a (${fmt(a)}):", style = MaterialTheme.typography.bodyMedium)
                Text("x = ${fmt(c - b)} / ${fmt(a)}", fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(4.dp))
                Text("Root Result:", fontWeight = FontWeight.SemiBold)
                Text("x = ${fmt(x)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun QuadraticEquationSolver() {
    var aStr by rememberSaveable { mutableStateOf("1") }
    var bStr by rememberSaveable { mutableStateOf("-5") }
    var cStr by rememberSaveable { mutableStateOf("6") }

    val a = aStr.toDoubleOrNull()
    val b = bStr.toDoubleOrNull()
    val c = cStr.toDoubleOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Quadratic Equation: a·x² + b·x + c = 0", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = aStr,
                    onValueChange = { aStr = it },
                    label = { Text("a") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = bStr,
                    onValueChange = { bStr = it },
                    label = { Text("b") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = cStr,
                    onValueChange = { cStr = it },
                    label = { Text("c") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Solution & Step-by-Step", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            if (a == null || b == null || c == null) {
                Text("Enter valid numbers for coefficients a, b, and c.", color = MaterialTheme.colorScheme.error)
            } else if (a == 0.0) {
                Text("Not a quadratic equation (a = 0). Degenerates to linear equation ${fmt(b)}x + ${fmt(c)} = 0.")
                if (b != 0.0) {
                    val root = -c / b
                    Text("x = ${fmt(root)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            } else {
                val discriminant = b * b - 4 * a * c
                val vertexX = -b / (2 * a)
                val vertexY = c - (b * b) / (4 * a)

                Text("Discriminant Δ = b² - 4ac:", style = MaterialTheme.typography.bodyMedium)
                Text("Δ = (${fmt(b)})² - 4·(${fmt(a)})·(${fmt(c)}) = ${fmt(discriminant)}", fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(4.dp))
                Text("Parabola Vertex (h, k): (${fmt(vertexX)}, ${fmt(vertexY)})", fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(4.dp))

                when {
                    discriminant > 0 -> {
                        val sqrtD = sqrt(discriminant)
                        val x1 = (-b + sqrtD) / (2 * a)
                        val x2 = (-b - sqrtD) / (2 * a)
                        Text("Two distinct real roots (Δ > 0):", fontWeight = FontWeight.SemiBold)
                        Text("x₁ = (-b + √Δ) / 2a = ${fmt(x1)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("x₂ = (-b - √Δ) / 2a = ${fmt(x2)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    discriminant == 0.0 -> {
                        val x = -b / (2 * a)
                        Text("One repeated real root (Δ = 0):", fontWeight = FontWeight.SemiBold)
                        Text("x = -b / 2a = ${fmt(x)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    else -> {
                        val realPart = -b / (2 * a)
                        val imagPart = sqrt(-discriminant) / (2 * abs(a))
                        Text("Two complex conjugate roots (Δ < 0):", fontWeight = FontWeight.SemiBold)
                        Text("x₁ = ${fmt(realPart)} + ${fmt(imagPart)}i", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("x₂ = ${fmt(realPart)} - ${fmt(imagPart)}i", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun System2x2Solver() {
    var a1Str by rememberSaveable { mutableStateOf("2") }
    var b1Str by rememberSaveable { mutableStateOf("3") }
    var c1Str by rememberSaveable { mutableStateOf("8") }

    var a2Str by rememberSaveable { mutableStateOf("5") }
    var b2Str by rememberSaveable { mutableStateOf("-1") }
    var c2Str by rememberSaveable { mutableStateOf("3") }

    val a1 = a1Str.toDoubleOrNull()
    val b1 = b1Str.toDoubleOrNull()
    val c1 = c1Str.toDoubleOrNull()
    val a2 = a2Str.toDoubleOrNull()
    val b2 = b2Str.toDoubleOrNull()
    val c2 = c2Str.toDoubleOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("System of 2 Linear Equations (Cramer's Rule)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Equation 1: a₁·x + b₁·y = c₁", style = MaterialTheme.typography.bodySmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = a1Str, onValueChange = { a1Str = it }, label = { Text("a₁") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = b1Str, onValueChange = { b1Str = it }, label = { Text("b₁") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = c1Str, onValueChange = { c1Str = it }, label = { Text("c₁") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            Text("Equation 2: a₂·x + b₂·y = c₂", style = MaterialTheme.typography.bodySmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = a2Str, onValueChange = { a2Str = it }, label = { Text("a₂") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = b2Str, onValueChange = { b2Str = it }, label = { Text("b₂") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = c2Str, onValueChange = { c2Str = it }, label = { Text("c₂") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Solution via Determinants", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            if (a1 == null || b1 == null || c1 == null || a2 == null || b2 == null || c2 == null) {
                Text("Enter valid numbers for all 6 parameters.", color = MaterialTheme.colorScheme.error)
            } else {
                val det = a1 * b2 - a2 * b1
                val detX = c1 * b2 - c2 * b1
                val detY = a1 * c2 - a2 * c1

                Text("Main determinant D = a₁b₂ - a₂b₁ = ${fmt(det)}", fontFamily = FontFamily.Monospace)
                Text("Dx = c₁b₂ - c₂b₁ = ${fmt(detX)}", fontFamily = FontFamily.Monospace)
                Text("Dy = a₁c₂ - a₂c₁ = ${fmt(detY)}", fontFamily = FontFamily.Monospace)

                if (det != 0.0) {
                    val x = detX / det
                    val y = detY / det
                    Spacer(Modifier.height(4.dp))
                    Text("Unique Solution:", fontWeight = FontWeight.SemiBold)
                    Text("x = Dx / D = ${fmt(x)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("y = Dy / D = ${fmt(y)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                } else {
                    if (detX == 0.0 && detY == 0.0) {
                        Text("Infinite Solutions: Dependent lines (same line).", color = MaterialTheme.colorScheme.primary)
                    } else {
                        Text("No Solution: Parallel lines with distinct intercepts.", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun CubicEquationSolver() {
    var aStr by rememberSaveable { mutableStateOf("1") }
    var bStr by rememberSaveable { mutableStateOf("-6") }
    var cStr by rememberSaveable { mutableStateOf("11") }
    var dStr by rememberSaveable { mutableStateOf("-6") }

    val a = aStr.toDoubleOrNull()
    val b = bStr.toDoubleOrNull()
    val c = cStr.toDoubleOrNull()
    val d = dStr.toDoubleOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Cubic Equation: a·x³ + b·x² + c·x + d = 0", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = aStr, onValueChange = { aStr = it }, label = { Text("a") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = bStr, onValueChange = { bStr = it }, label = { Text("b") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = cStr, onValueChange = { cStr = it }, label = { Text("c") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = dStr, onValueChange = { dStr = it }, label = { Text("d") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Cubic Roots (Cardano's Formula)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            if (a == null || b == null || c == null || d == null) {
                Text("Enter valid coefficients.", color = MaterialTheme.colorScheme.error)
            } else if (a == 0.0) {
                Text("Leading coefficient a = 0 (reduced to quadratic equation).", color = MaterialTheme.colorScheme.error)
            } else {
                val roots = solveCubic(a, b, c, d)
                roots.forEachIndexed { idx, root ->
                    Text("x${idx + 1} = $root", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

/**
 * Solves cubic ax³ + bx² + cx + d = 0 using depressed cubic t³ + pt + q = 0
 */
fun solveCubic(a: Double, b: Double, c: Double, d: Double): List<String> {
    val an = b / a
    val bn = c / a
    val cn = d / a

    val p = bn - an * an / 3.0
    val q = (2.0 * an * an * an / 27.0) - (an * bn / 3.0) + cn
    val delta = (q * q / 4.0) + (p * p * p / 27.0)

    val shift = an / 3.0

    return if (delta > 0) {
        val u = cbrt(-q / 2.0 + sqrt(delta))
        val v = cbrt(-q / 2.0 - sqrt(delta))
        val x1 = u + v - shift
        val real = -(u + v) / 2.0 - shift
        val imag = sqrt(3.0) / 2.0 * (u - v)
        listOf(
            fmt(x1),
            "${fmt(real)} + ${fmt(abs(imag))}i",
            "${fmt(real)} - ${fmt(abs(imag))}i",
        )
    } else if (abs(delta) < 1e-9) {
        val u = cbrt(-q / 2.0)
        val x1 = 2.0 * u - shift
        val x2 = -u - shift
        listOf(fmt(x1), fmt(x2), fmt(x2))
    } else {
        // 3 real roots
        val r = sqrt(-p * p * p / 27.0)
        val phi = acos((-q / (2.0 * r)).coerceIn(-1.0, 1.0))
        val m = 2.0 * sqrt(-p / 3.0)
        val x1 = m * cos(phi / 3.0) - shift
        val x2 = m * cos((phi + 2 * Math.PI) / 3.0) - shift
        val x3 = m * cos((phi + 4 * Math.PI) / 3.0) - shift
        listOf(fmt(x1), fmt(x2), fmt(x3))
    }
}

private fun cbrt(x: Double): Double = if (x < 0) -(-x).pow(1.0 / 3.0) else x.pow(1.0 / 3.0)

private fun fmt(v: Double): String {
    if (abs(v) < 1e-9) return "0"
    return if (v == v.toLong().toDouble() && abs(v) < 1e9) {
        v.toLong().toString()
    } else {
        "%.4f".format(v).trimEnd('0').trimEnd('.')
    }
}
