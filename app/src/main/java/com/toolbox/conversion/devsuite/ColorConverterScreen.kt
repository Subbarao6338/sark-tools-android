package com.toolbox.conversion.devsuite

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import kotlin.math.roundToInt

@Composable
fun ColorConverterScreen() {
    val context = LocalContext.current

    var red by rememberSaveable { mutableFloatStateOf(66f) }
    var green by rememberSaveable { mutableFloatStateOf(133f) }
    var blue by rememberSaveable { mutableFloatStateOf(244f) }

    val r = red.roundToInt().coerceIn(0, 255)
    val g = green.roundToInt().coerceIn(0, 255)
    val b = blue.roundToInt().coerceIn(0, 255)

    val composeColor = Color(r, g, b)

    val hex = String.format("#%02X%02X%02X", r, g, b)
    val rgb = "rgb($r, $g, $b)"

    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(r, g, b, hsv)
    val hsvStr = "hsv(${hsv[0].roundToInt()}°, ${(hsv[1] * 100).roundToInt()}%, ${(hsv[2] * 100).roundToInt()}%)"

    val rP = r / 255f; val gP = g / 255f; val bP = b / 255f
    val cMax = maxOf(rP, gP, bP); val cMin = minOf(rP, gP, bP)
    val delta = cMax - cMin
    val lum = (cMax + cMin) / 2f
    val sat = if (delta == 0f) 0f else delta / (1f - kotlin.math.abs(2f * lum - 1f))
    val hue = when {
        delta == 0f -> 0f
        cMax == rP -> (60f * (((gP - bP) / delta) % 6f) + 360f) % 360f
        cMax == gP -> (60f * (((bP - rP) / delta) + 2f) + 360f) % 360f
        else -> (60f * (((rP - gP) / delta) + 4f) + 360f) % 360f
    }
    val hslStr = "hsl(${hue.roundToInt()}°, ${(sat * 100).roundToInt()}%, ${(lum * 100).roundToInt()}%)"

    val k = 1f - cMax
    val cmykStr = if (k >= 1f) {
        "cmyk(0%, 0%, 0%, 100%)"
    } else {
        val c = (1f - rP - k) / (1f - k)
        val m = (1f - gP - k) / (1f - k)
        val y = (1f - bP - k) / (1f - k)
        "cmyk(${(c * 100).roundToInt()}%, ${(m * 100).roundToInt()}%, ${(y * 100).roundToInt()}%, ${(k * 100).roundToInt()}%)"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(composeColor)
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = hex,
                color = if (lum > 0.5f) Color.Black else Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Red: $r")
                Slider(value = red, onValueChange = { red = it }, valueRange = 0f..255f)

                Text("Green: $g")
                Slider(value = green, onValueChange = { green = it }, valueRange = 0f..255f)

                Text("Blue: $b")
                Slider(value = blue, onValueChange = { blue = it }, valueRange = 0f..255f)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Color Formats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                ColorFormatRow("HEX", hex) { copyToClipboard(context, "HEX", hex) }
                ColorFormatRow("RGB", rgb) { copyToClipboard(context, "RGB", rgb) }
                ColorFormatRow("HSL", hslStr) { copyToClipboard(context, "HSL", hslStr) }
                ColorFormatRow("HSV", hsvStr) { copyToClipboard(context, "HSV", hsvStr) }
                ColorFormatRow("CMYK", cmykStr) { copyToClipboard(context, "CMYK", cmykStr) }
            }
        }
    }
}

@Composable
private fun ColorFormatRow(label: String, value: String, onCopy: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(onClick = onCopy) {
            Text("Copy")
        }
    }
}
