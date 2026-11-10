package com.toolbox.devtools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@Composable
fun ColorContrastMatrixScreen() {
    var fgHex by remember { mutableStateOf("#000000") }
    var bgHex by remember { mutableStateOf("#FFFFFF") }

    fun parseColor(hex: String, default: Color): Color {
        return try {
            val clean = hex.removePrefix("#")
            val parsed = clean.toLong(16)
            if (clean.length == 6) Color(0xFF000000 or parsed) else Color(parsed)
        } catch (_: Exception) {
            default
        }
    }

    fun luminance(c: Color): Double {
        fun channel(v: Float): Double {
            return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).toDouble().pow(2.4)
        }
        return 0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)
    }

    val fg = parseColor(fgHex, Color.Black)
    val bg = parseColor(bgHex, Color.White)

    val l1 = luminance(fg)
    val l2 = luminance(bg)
    val ratio = (max(l1, l2) + 0.05) / (min(l1, l2) + 0.05)

    val aaNormal = ratio >= 4.5
    val aaLarge = ratio >= 3.0
    val aaaNormal = ratio >= 7.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Color Contrast Matrix & WCAG Evaluator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Check relative luminance and WCAG 2.1 AA/AAA accessibility compliance ratings.")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = fgHex,
                onValueChange = { fgHex = it },
                label = { Text("Foreground Hex") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = bgHex,
                onValueChange = { bgHex = it },
                label = { Text("Background Hex") },
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(bg, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Sample Contrast Text", color = fg, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        }

        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Calculated Contrast Ratio", style = MaterialTheme.typography.labelMedium)
                Text(String.format("%.2f : 1", ratio), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("WCAG Compliance Checklist:", fontWeight = FontWeight.SemiBold)
                Text("• AA Normal Text (≥ 4.5:1): " + if (aaNormal) "PASS" else "FAIL", fontWeight = FontWeight.Bold)
                Text("• AA Large Text (≥ 3.0:1): " + if (aaLarge) "PASS" else "FAIL", fontWeight = FontWeight.Bold)
                Text("• AAA Normal Text (≥ 7.0:1): " + if (aaaNormal) "PASS" else "FAIL", fontWeight = FontWeight.Bold)
            }
        }
    }
}
