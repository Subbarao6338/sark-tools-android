package com.toolbox.conversion.geometry

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
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.math.sqrt
import kotlin.math.tan

@Composable
fun GeometryCalculatorScreen() {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val sections = listOf("Right Triangle (3-4-5)", "2D Shapes", "3D Bodies", "Construction & Tiles")

    Column(modifier = Modifier.fillMaxSize()) {
        TileSelector(
            options = sections,
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
                0 -> RightTriangleCalculator()
                1 -> Shapes2DCalculator()
                2 -> Shapes3DCalculator()
                3 -> ConstructionCalculator()
            }
        }
    }
}

@Composable
private fun RightTriangleCalculator() {
    var legAStr by rememberSaveable { mutableStateOf("3") }
    var legBStr by rememberSaveable { mutableStateOf("4") }

    val a = legAStr.toDoubleOrNull()
    val b = legBStr.toDoubleOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Right Triangle (Pythagorean Theorem)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Enter two perpendicular legs a and b to compute hypotenuse c, angles, area, and perimeter.", style = MaterialTheme.typography.bodySmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = legAStr,
                    onValueChange = { legAStr = it },
                    label = { Text("Leg a") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = legBStr,
                    onValueChange = { legBStr = it },
                    label = { Text("Leg b") },
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
            Text("Calculated Properties", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            if (a == null || b == null || a <= 0 || b <= 0) {
                Text("Enter positive real values for legs a and b.", color = MaterialTheme.colorScheme.error)
            } else {
                val c = hypot(a, b)
                val area = 0.5 * a * b
                val perimeter = a + b + c
                val angleADeg = Math.toDegrees(atan(a / b))
                val angleBDeg = 90.0 - angleADeg

                Text("Hypotenuse c: √(a² + b²)", style = MaterialTheme.typography.bodyMedium)
                Text("c = ${fmt(c)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text("Area: ½ · a · b = ${fmt(area)}", fontFamily = FontFamily.Monospace)
                Text("Perimeter: a + b + c = ${fmt(perimeter)}", fontFamily = FontFamily.Monospace)
                Text("Angle α: ${fmt(angleADeg)}°", fontFamily = FontFamily.Monospace)
                Text("Angle β: ${fmt(angleBDeg)}°", fontFamily = FontFamily.Monospace)
                Text("Angle γ: 90.0°", fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun Shapes2DCalculator() {
    var shapeIndex by rememberSaveable { mutableIntStateOf(0) }
    val shapes = listOf("Circle", "Rectangle", "Triangle", "Trapezoid")

    var val1 by rememberSaveable { mutableStateOf("5") }
    var val2 by rememberSaveable { mutableStateOf("10") }
    var val3 by rememberSaveable { mutableStateOf("4") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("2D Geometric Shapes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TileSelector(options = shapes, selectedIndex = shapeIndex, onSelectIndex = { shapeIndex = it })

            when (shapeIndex) {
                0 -> { // Circle
                    OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Radius r") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                1 -> { // Rectangle
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Length") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = val2, onValueChange = { val2 = it }, label = { Text("Width") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                }
                2 -> { // Triangle
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Base") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = val2, onValueChange = { val2 = it }, label = { Text("Height") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                }
                3 -> { // Trapezoid
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Base a") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = val2, onValueChange = { val2 = it }, label = { Text("Base b") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = val3, onValueChange = { val3 = it }, label = { Text("Height h") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Area & Perimeter", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            val d1 = val1.toDoubleOrNull() ?: 0.0
            val d2 = val2.toDoubleOrNull() ?: 0.0
            val d3 = val3.toDoubleOrNull() ?: 0.0

            when (shapeIndex) {
                0 -> {
                    val area = PI * d1 * d1
                    val circumference = 2 * PI * d1
                    Text("Area: π·r² = ${fmt(area)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Circumference: 2·π·r = ${fmt(circumference)}", fontFamily = FontFamily.Monospace)
                    Text("Diameter: 2·r = ${fmt(2 * d1)}", fontFamily = FontFamily.Monospace)
                }
                1 -> {
                    val area = d1 * d2
                    val perimeter = 2 * (d1 + d2)
                    Text("Area: L · W = ${fmt(area)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Perimeter: 2(L + W) = ${fmt(perimeter)}", fontFamily = FontFamily.Monospace)
                    Text("Diagonal: √(L² + W²) = ${fmt(hypot(d1, d2))}", fontFamily = FontFamily.Monospace)
                }
                2 -> {
                    val area = 0.5 * d1 * d2
                    Text("Area: ½ · b · h = ${fmt(area)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                3 -> {
                    val area = 0.5 * (d1 + d2) * d3
                    Text("Area: ½ · (a + b) · h = ${fmt(area)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun Shapes3DCalculator() {
    var bodyIndex by rememberSaveable { mutableIntStateOf(0) }
    val bodies = listOf("Sphere", "Cylinder", "Cone", "Box / Prism")

    var val1 by rememberSaveable { mutableStateOf("3") }
    var val2 by rememberSaveable { mutableStateOf("7") }
    var val3 by rememberSaveable { mutableStateOf("4") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("3D Solids & Bodies", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TileSelector(options = bodies, selectedIndex = bodyIndex, onSelectIndex = { bodyIndex = it })

            when (bodyIndex) {
                0 -> OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Radius r") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                1 -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Radius r") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = val2, onValueChange = { val2 = it }, label = { Text("Height h") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                2 -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Radius r") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = val2, onValueChange = { val2 = it }, label = { Text("Height h") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                3 -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Length L") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = val2, onValueChange = { val2 = it }, label = { Text("Width W") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = val3, onValueChange = { val3 = it }, label = { Text("Height H") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Volume & Surface Area", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            val d1 = val1.toDoubleOrNull() ?: 0.0
            val d2 = val2.toDoubleOrNull() ?: 0.0
            val d3 = val3.toDoubleOrNull() ?: 0.0

            when (bodyIndex) {
                0 -> { // Sphere
                    val volume = (4.0 / 3.0) * PI * d1 * d1 * d1
                    val surface = 4 * PI * d1 * d1
                    Text("Volume: ⁴/₃·π·r³ = ${fmt(volume)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Surface Area: 4·π·r² = ${fmt(surface)}", fontFamily = FontFamily.Monospace)
                }
                1 -> { // Cylinder
                    val volume = PI * d1 * d1 * d2
                    val lateralArea = 2 * PI * d1 * d2
                    val totalArea = lateralArea + 2 * PI * d1 * d1
                    Text("Volume: π·r²·h = ${fmt(volume)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Lateral Area: 2·π·r·h = ${fmt(lateralArea)}", fontFamily = FontFamily.Monospace)
                    Text("Total Surface Area: ${fmt(totalArea)}", fontFamily = FontFamily.Monospace)
                }
                2 -> { // Cone
                    val volume = (1.0 / 3.0) * PI * d1 * d1 * d2
                    val slant = hypot(d1, d2)
                    val totalArea = PI * d1 * (d1 + slant)
                    Text("Volume: ⅓·π·r²·h = ${fmt(volume)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Slant Height: √(r² + h²) = ${fmt(slant)}", fontFamily = FontFamily.Monospace)
                    Text("Surface Area: π·r·(r + s) = ${fmt(totalArea)}", fontFamily = FontFamily.Monospace)
                }
                3 -> { // Box
                    val volume = d1 * d2 * d3
                    val surface = 2 * (d1 * d2 + d2 * d3 + d1 * d3)
                    Text("Volume: L · W · H = ${fmt(volume)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Surface Area: 2(LW + WH + LH) = ${fmt(surface)}", fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun ConstructionCalculator() {
    var modeIndex by rememberSaveable { mutableIntStateOf(0) }
    val modes = listOf("Tiles & Flooring", "Concrete Slab")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Construction Estimator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TileSelector(options = modes, selectedIndex = modeIndex, onSelectIndex = { modeIndex = it })

            if (modeIndex == 0) { // Tiles
                var roomL by rememberSaveable { mutableStateOf("4") }
                var roomW by rememberSaveable { mutableStateOf("3") }
                var tileL by rememberSaveable { mutableStateOf("30") } // cm
                var tileW by rememberSaveable { mutableStateOf("30") } // cm
                var wastePct by rememberSaveable { mutableStateOf("10") } // %

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = roomL, onValueChange = { roomL = it }, label = { Text("Room Length (m)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = roomW, onValueChange = { roomW = it }, label = { Text("Room Width (m)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = tileL, onValueChange = { tileL = it }, label = { Text("Tile Length (cm)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = tileW, onValueChange = { tileW = it }, label = { Text("Tile Width (cm)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                OutlinedTextField(value = wastePct, onValueChange = { wastePct = it }, label = { Text("Waste Margin (%)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                val rL = roomL.toDoubleOrNull() ?: 0.0
                val rW = roomW.toDoubleOrNull() ?: 0.0
                val tL = (tileL.toDoubleOrNull() ?: 0.0) / 100.0
                val tW = (tileW.toDoubleOrNull() ?: 0.0) / 100.0
                val waste = (wastePct.toDoubleOrNull() ?: 10.0) / 100.0

                val roomArea = rL * rW
                val tileArea = tL * tW
                val baseTiles = if (tileArea > 0) roomArea / tileArea else 0.0
                val totalTiles = ceil(baseTiles * (1.0 + waste)).toLong()

                Spacer(Modifier.height(8.dp))
                Text("Tiles Required (with ${fmt(waste * 100)}% buffer):", style = MaterialTheme.typography.titleSmall)
                Text("$totalTiles tiles", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Room Area: ${fmt(roomArea)} m² | Single Tile: ${fmt(tileArea)} m²", fontFamily = FontFamily.Monospace)
            } else { // Concrete
                var slabL by rememberSaveable { mutableStateOf("5") } // m
                var slabW by rememberSaveable { mutableStateOf("3") } // m
                var thicknessCm by rememberSaveable { mutableStateOf("10") } // cm

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = slabL, onValueChange = { slabL = it }, label = { Text("Length (m)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = slabW, onValueChange = { slabW = it }, label = { Text("Width (m)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                OutlinedTextField(value = thicknessCm, onValueChange = { thicknessCm = it }, label = { Text("Thickness (cm)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                val sL = slabL.toDoubleOrNull() ?: 0.0
                val sW = slabW.toDoubleOrNull() ?: 0.0
                val thickM = (thicknessCm.toDoubleOrNull() ?: 0.0) / 100.0

                val volumeM3 = sL * sW * thickM
                // Typical 50kg cement bag produces approx 0.035 m³ concrete with 1:2:4 ratio, or ready-mix bags (25kg = ~0.012 m³)
                val bags25kg = ceil(volumeM3 / 0.012).toLong()

                Spacer(Modifier.height(8.dp))
                Text("Concrete Volume Needed:", style = MaterialTheme.typography.titleSmall)
                Text("${fmt(volumeM3)} m³", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Pre-mixed Premix Bags (25 kg / 0.012 m³): ~$bags25kg bags", fontFamily = FontFamily.Monospace)
            }
        }
    }
}

private fun fmt(v: Double): String {
    return if (v == v.toLong().toDouble() && v < 1e9) {
        v.toLong().toString()
    } else {
        "%.3f".format(v).trimEnd('0').trimEnd('.')
    }
}
