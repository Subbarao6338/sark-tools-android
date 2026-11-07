package com.toolbox.conversion.contrast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object ContrastCalculatorEngine {
    /**
     * WCAG 2.1 Relative Luminance formula:
     * L = 0.2126 * R + 0.7152 * G + 0.0722 * B
     * where each channel is linearized.
     */
    fun linearize(channel: Float): Double {
        val c = channel.toDouble().coerceIn(0.0, 1.0)
        return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
    }

    fun luminance(r: Float, g: Float, b: Float): Double {
        return 0.2126 * linearize(r) + 0.7152 * linearize(g) + 0.0722 * linearize(b)
    }

    fun contrastRatio(color1: Color, color2: Color): Double {
        val l1 = luminance(color1.red, color1.green, color1.blue)
        val l2 = luminance(color2.red, color2.green, color2.blue)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }
}

@Composable
fun ContrastCheckerScreen() {
    var fgRed by remember { mutableFloatStateOf(0.1f) }
    var fgGreen by remember { mutableFloatStateOf(0.1f) }
    var fgBlue by remember { mutableFloatStateOf(0.1f) }

    var bgRed by remember { mutableFloatStateOf(0.95f) }
    var bgGreen by remember { mutableFloatStateOf(0.95f) }
    var bgBlue by remember { mutableFloatStateOf(0.95f) }

    val fgColor = Color(fgRed, fgGreen, fgBlue)
    val bgColor = Color(bgRed, bgGreen, bgBlue)

    val ratio = ContrastCalculatorEngine.contrastRatio(fgColor, bgColor)
    val formattedRatio = String.format("%.2f:1", ratio)

    // WCAG thresholds
    val aaNormal = ratio >= 4.5
    val aaLarge = ratio >= 3.0
    val aaaNormal = ratio >= 7.0
    val aaaLarge = ratio >= 4.5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Live Preview Box
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Sample Headline (Large)",
                        color = fgColor,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The quick brown fox jumps over the lazy dog. Normal body text (14-16sp).",
                        color = fgColor,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        // Contrast Ratio Score Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Contrast Ratio",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                    Text(
                        text = formattedRatio,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                IconButton(onClick = {
                    val tempR = fgRed; val tempG = fgGreen; val tempB = fgBlue
                    fgRed = bgRed; fgGreen = bgGreen; fgBlue = bgBlue
                    bgRed = tempR; bgGreen = tempG; bgBlue = tempB
                }) {
                    Icon(
                        Icons.Default.SwapVert,
                        contentDescription = "Swap Colors",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        // WCAG Standards Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "WCAG 2.1 Compliance Status",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                ComplianceRow("WCAG AA (Normal Text >= 4.5:1)", aaNormal)
                ComplianceRow("WCAG AA (Large Text >= 3.0:1)", aaLarge)
                ComplianceRow("WCAG AAA (Normal Text >= 7.0:1)", aaaNormal)
                ComplianceRow("WCAG AAA (Large Text >= 4.5:1)", aaaLarge)
            }
        }

        // Text (Foreground) Color Adjuster
        ColorSlidersCard(
            title = "Text (Foreground) Color",
            red = fgRed,
            green = fgGreen,
            blue = fgBlue,
            onRedChange = { fgRed = it },
            onGreenChange = { fgGreen = it },
            onBlueChange = { fgBlue = it },
        )

        // Background Color Adjuster
        ColorSlidersCard(
            title = "Background Color",
            red = bgRed,
            green = bgGreen,
            blue = bgBlue,
            onRedChange = { bgRed = it },
            onGreenChange = { bgGreen = it },
            onBlueChange = { bgBlue = it },
        )
    }
}

@Composable
private fun ComplianceRow(label: String, pass: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (pass) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (pass) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (pass) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (pass) "PASS" else "FAIL",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (pass) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun ColorSlidersCard(
    title: String,
    red: Float,
    green: Float,
    blue: Float,
    onRedChange: (Float) -> Unit,
    onGreenChange: (Float) -> Unit,
    onBlueChange: (Float) -> Unit,
) {
    val hex = String.format("#%02X%02X%02X", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(red, green, blue),
                    modifier = Modifier.size(24.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp)),
                ) {}
            }
            Text("Hex: $hex", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            SliderRow("R: ${(red * 255).toInt()}", red, onRedChange)
            SliderRow("G: ${(green * 255).toInt()}", green, onGreenChange)
            SliderRow("B: ${(blue * 255).toInt()}", blue, onBlueChange)
        }
    }
}

@Composable
private fun SliderRow(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(52.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
        )
    }
}
