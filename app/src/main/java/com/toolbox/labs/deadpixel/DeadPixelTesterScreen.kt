package com.toolbox.labs.deadpixel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DeadPixelTesterScreen() {
    val testColors = remember {
        listOf(
            Color.Red to "Pure Red (Sub-pixel test)",
            Color.Green to "Pure Green (Sub-pixel test)",
            Color.Blue to "Pure Blue (Sub-pixel test)",
            Color.White to "Pure White (Dark/Dead pixel test)",
            Color.Black to "Pure Black (Stuck/Lit pixel test)",
            Color.Yellow to "Yellow (RGB mix test)",
            Color.Cyan to "Cyan (RGB mix test)",
            Color.Magenta to "Magenta (RGB mix test)",
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val (currentColor, colorLabel) = testColors[currentIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentColor)
            .clickable {
                currentIndex = (currentIndex + 1) % testColors.size
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = colorLabel,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Tap screen to cycle color (${currentIndex + 1}/${testColors.size})",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
