package com.toolbox.labs.dimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class DimmerPreset(val label: String, val alpha: Float, val warmTint: Float) {
    Reading("Reading", 0.3f, 0.4f),
    Bedtime("Bedtime", 0.6f, 0.7f),
    DeepNight("Deep Night", 0.85f, 0.9f),
    Cinema("Cinema", 0.5f, 0.1f)
}

@Composable
fun ScreenDimmerScreen() {
    var dimLevel by remember { mutableFloatStateOf(0.4f) } // 0.0 to 0.9
    var warmthLevel by remember { mutableFloatStateOf(0.5f) } // 0.0 to 1.0 (amber tint)
    var isFilterActive by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bedtime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Night Dimmer Filter", style = MaterialTheme.typography.titleMedium)
                        }
                        FilterChip(
                            selected = isFilterActive,
                            onClick = { isFilterActive = !isFilterActive },
                            label = { Text(if (isFilterActive) "Enabled" else "Disabled") },
                        )
                    }

                    Text("Darkness Overlay: ${(dimLevel * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = dimLevel,
                        onValueChange = { dimLevel = it },
                        valueRange = 0.0f..0.9f,
                    )

                    Text("Blue Light Warmth (Amber): ${(warmthLevel * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = warmthLevel,
                        onValueChange = { warmthLevel = it },
                        valueRange = 0.0f..1.0f,
                    )
                }
            }

            Text("Quick Night Presets", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DimmerPreset.values().forEach { preset ->
                    FilterChip(
                        selected = dimLevel == preset.alpha && warmthLevel == preset.warmTint,
                        onClick = {
                            dimLevel = preset.alpha
                            warmthLevel = preset.warmTint
                            isFilterActive = true
                        },
                        label = { Text(preset.label) },
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("About Dimmer & Warmth", style = MaterialTheme.typography.labelLarge)
                    Text(
                        "Reduces eye strain in dark environments by dimming below system minimum brightness and applying warm color temperature filtering.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        // The actual Dimmer / Warmth overlay layer
        if (isFilterActive) {
            val warmRed = 1.0f
            val warmGreen = 0.6f * warmthLevel + (1f - warmthLevel)
            val warmBlue = 0.2f * warmthLevel + (1f - warmthLevel)
            val overlayColor = Color(
                red = 0.1f * (1f - warmthLevel) + 0.3f * warmthLevel,
                green = 0.05f * (1f - warmthLevel) + 0.15f * warmthLevel,
                blue = 0.0f,
                alpha = dimLevel,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayColor)
            )
        }
    }
}
