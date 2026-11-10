package com.toolbox.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AspectRatioScreen() {
    var widthInput by remember { mutableStateOf("1920") }
    var heightInput by remember { mutableStateOf("1080") }
    var targetWidth by remember { mutableStateOf("1280") }
    var computedHeight by remember { mutableStateOf("720") }

    fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    val w = widthInput.toLongOrNull() ?: 1L
    val h = heightInput.toLongOrNull() ?: 1L
    val d = gcd(w, h).coerceAtLeast(1L)
    val ratioW = w / d
    val ratioH = h / d

    fun recomputeTarget(newWStr: String) {
        targetWidth = newWStr
        val tw = newWStr.toDoubleOrNull()
        if (tw != null && w > 0) {
            val calcH = (tw * h) / w
            computedHeight = String.format("%.1f", calcH)
        }
    }

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
                Text("Aspect Ratio Pro Scaler", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Analyze dimension proportions, calculate simplified aspect ratios, and rescale resolutions proportionally.")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = widthInput == "1920" && heightInput == "1080", onClick = {
                widthInput = "1920"; heightInput = "1080"; recomputeTarget(targetWidth)
            }, label = { Text("16:9 (FHD)") })

            FilterChip(selected = widthInput == "1080" && heightInput == "1920", onClick = {
                widthInput = "1080"; heightInput = "1920"; recomputeTarget(targetWidth)
            }, label = { Text("9:16 (Reel)") })

            FilterChip(selected = widthInput == "1080" && heightInput == "1080", onClick = {
                widthInput = "1080"; heightInput = "1080"; recomputeTarget(targetWidth)
            }, label = { Text("1:1 (Square)") })
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = widthInput,
                onValueChange = { widthInput = it; recomputeTarget(targetWidth) },
                label = { Text("Base Width (px)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = heightInput,
                onValueChange = { heightInput = it; recomputeTarget(targetWidth) },
                label = { Text("Base Height (px)") },
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Proportional Ratio", style = MaterialTheme.typography.labelMedium)
                Text("$ratioW : $ratioH", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Text("Proportional Resizer", fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = targetWidth,
                onValueChange = { recomputeTarget(it) },
                label = { Text("Target Width") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = computedHeight,
                onValueChange = {},
                readOnly = true,
                label = { Text("Scaled Height") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
