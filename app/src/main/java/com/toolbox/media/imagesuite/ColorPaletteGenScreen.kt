package com.toolbox.media.imagesuite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun ColorPaletteGenScreen() {
    val clipboardManager = LocalClipboardManager.current
    var hue by remember { mutableStateOf(210f) }
    var saturation by remember { mutableStateOf(0.75f) }
    var lightness by remember { mutableStateOf(0.50f) }
    var harmonyMode by remember { mutableStateOf(0) }

    fun hslToColor(h: Float, s: Float, l: Float): Color {
        val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
        val hp = ((h % 360f) + 360f) % 360f / 60f
        val x = c * (1f - kotlin.math.abs((hp % 2f) - 1f))
        val (r1, g1, b1) = when {
            hp < 1f -> Triple(c, x, 0f)
            hp < 2f -> Triple(x, c, 0f)
            hp < 3f -> Triple(0f, c, x)
            hp < 4f -> Triple(0f, x, c)
            hp < 5f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        val m = l - c / 2f
        return Color(r1 + m, g1 + m, b1 + m)
    }

    fun colorToHex(color: Color): String {
        val r = (color.red * 255).toInt().coerceIn(0, 255)
        val g = (color.green * 255).toInt().coerceIn(0, 255)
        val b = (color.blue * 255).toInt().coerceIn(0, 255)
        return String.format(Locale.US, "#%02X%02X%02X", r, g, b)
    }

    val palette = remember(hue, saturation, lightness, harmonyMode) {
        when (harmonyMode) {
            0 -> listOf(
                hslToColor(hue, saturation, lightness),
                hslToColor(hue + 180f, saturation, lightness),
                hslToColor(hue, saturation * 0.5f, lightness + 0.2f),
                hslToColor(hue + 180f, saturation * 0.5f, lightness - 0.2f)
            )
            1 -> listOf(
                hslToColor(hue - 30f, saturation, lightness),
                hslToColor(hue, saturation, lightness),
                hslToColor(hue + 30f, saturation, lightness),
                hslToColor(hue + 60f, saturation, lightness)
            )
            2 -> listOf(
                hslToColor(hue, saturation, lightness),
                hslToColor(hue + 120f, saturation, lightness),
                hslToColor(hue + 240f, saturation, lightness),
                hslToColor(hue, saturation * 0.4f, (lightness + 0.25f).coerceIn(0f, 1f))
            )
            else -> listOf(
                hslToColor(hue, saturation, 0.20f),
                hslToColor(hue, saturation, 0.40f),
                hslToColor(hue, saturation, 0.60f),
                hslToColor(hue, saturation, 0.80f)
            )
        }
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
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                palette.forEach { color ->
                    val hex = colorToHex(color)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(color)
                            .clickable { clipboardManager.setText(AnnotatedString(hex)) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = hex,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Text("Color Harmony Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Complementary", "Analogous", "Triadic", "Mono").forEachIndexed { index, label ->
                FilterChip(
                    selected = harmonyMode == index,
                    onClick = { harmonyMode = index },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text("Base Hue (${hue.toInt()}°)", fontWeight = FontWeight.SemiBold)
        Slider(
            value = hue,
            onValueChange = { hue = it },
            valueRange = 0f..360f,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Saturation (${(saturation * 100).toInt()}%)", fontWeight = FontWeight.SemiBold)
        Slider(
            value = saturation,
            onValueChange = { saturation = it },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Lightness (${(lightness * 100).toInt()}%)", fontWeight = FontWeight.SemiBold)
        Slider(
            value = lightness,
            onValueChange = { lightness = it },
            valueRange = 0.1f..0.9f,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val hexList = palette.joinToString(", ") { colorToHex(it) }
                clipboardManager.setText(AnnotatedString(hexList))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Palette, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Copy All Palette Hex Codes")
        }
    }
}
