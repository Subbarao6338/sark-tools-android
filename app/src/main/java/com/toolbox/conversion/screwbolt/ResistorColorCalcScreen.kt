package com.toolbox.conversion.screwbolt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun ResistorColorCalcScreen() {
    data class Band(val name: String, val color: Color, val digit: Int, val multiplier: Double, val tolerance: String)

    val bands = listOf(
        Band("Black", Color(0xFF000000), 0, 1.0, "—"),
        Band("Brown", Color(0xFF795548), 1, 10.0, "±1%"),
        Band("Red", Color(0xFFD32F2F), 2, 100.0, "±2%"),
        Band("Orange", Color(0xFFFF9800), 3, 1000.0, "—"),
        Band("Yellow", Color(0xFFFFEB3B), 4, 10000.0, "—"),
        Band("Green", Color(0xFF4CAF50), 5, 100000.0, "±0.5%"),
        Band("Blue", Color(0xFF2196F3), 6, 1000000.0, "±0.25%"),
        Band("Violet", Color(0xFF9C27B0), 7, 10000000.0, "±0.1%"),
        Band("Grey", Color(0xFF9E9E9E), 8, 100000000.0, "±0.05%"),
        Band("White", Color(0xFFFFFFFF), 9, 1000000000.0, "—"),
        Band("Gold", Color(0xFFFFD700), -1, 0.1, "±5%"),
        Band("Silver", Color(0xFFC0C0C0), -1, 0.01, "±10%")
    )

    var band1Idx by remember { mutableStateOf(1) }
    var band2Idx by remember { mutableStateOf(0) }
    var multiplierIdx by remember { mutableStateOf(2) }
    var toleranceIdx by remember { mutableStateOf(10) }

    val b1 = bands[band1Idx]
    val b2 = bands[band2Idx]
    val bm = bands[multiplierIdx]
    val bt = bands[toleranceIdx]

    val baseValue = (b1.digit * 10) + b2.digit
    val totalOhms = baseValue * bm.multiplier

    val formattedResistance = when {
        totalOhms >= 1_000_000 -> String.format(Locale.US, "%.2f MΩ", totalOhms / 1_000_000)
        totalOhms >= 1_000 -> String.format(Locale.US, "%.2f kΩ", totalOhms / 1_000)
        else -> String.format(Locale.US, "%.1f Ω", totalOhms)
    }

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
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(36.dp)
                        .background(Color(0xFFE0C49F), RoundedCornerShape(8.dp))
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(12.dp).fillMaxHeight().background(b1.color))
                    Box(modifier = Modifier.width(12.dp).fillMaxHeight().background(b2.color))
                    Box(modifier = Modifier.width(12.dp).fillMaxHeight().background(bm.color))
                    Box(modifier = Modifier.width(12.dp).fillMaxHeight().background(bt.color))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "$formattedResistance ${bt.tolerance}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("4-Band Resistor Value", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Text("Band 1: First Digit (${b1.name})", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            bands.take(10).forEachIndexed { index, band ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(band.color)
                        .clickable { band1Idx = index }
                )
            }
        }

        Text("Band 2: Second Digit (${b2.name})", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            bands.take(10).forEachIndexed { index, band ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(band.color)
                        .clickable { band2Idx = index }
                )
            }
        }

        Text("Band 3: Multiplier (${bm.name})", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            bands.forEachIndexed { index, band ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(band.color)
                        .clickable { multiplierIdx = index }
                )
            }
        }

        Text("Band 4: Tolerance (${bt.name} - ${bt.tolerance})", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1 to "Brown (±1%)", 2 to "Red (±2%)", 10 to "Gold (±5%)", 11 to "Silver (±10%)").forEach { (idx, label) ->
                FilterChip(
                    selected = toleranceIdx == idx,
                    onClick = { toleranceIdx = idx },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
