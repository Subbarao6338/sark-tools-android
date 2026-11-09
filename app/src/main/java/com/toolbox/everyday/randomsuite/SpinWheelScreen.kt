package com.toolbox.everyday.randomsuite

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SpinWheelScreen() {
    val scope = rememberCoroutineScope()
    var optionsText by rememberSaveable { mutableStateOf("Pizza\nBurger\nSushi\nTacos\nSalad\nPasta") }
    val rotation = remember { Animatable(0f) }
    var selectedWinner by rememberSaveable { mutableStateOf<String?>(null) }
    var isSpinning by remember { mutableStateOf(false) }

    val options = optionsText.lines().map { it.trim() }.filter { it.isNotEmpty() }

    val colors = listOf(
        Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047),
        Color(0xFFFB8C00), Color(0xFF8E24AA), Color(0xFF00ACC1),
        Color(0xFFFDD835), Color(0xFFD81B60), Color(0xFF3949AB),
    )

    fun spin() {
        if (options.isEmpty() || isSpinning) return
        isSpinning = true
        selectedWinner = null

        val randomExtraTurns = Random.nextInt(5, 10) * 360f
        val randomAngle = Random.nextFloat() * 360f
        val targetRotation = rotation.value + randomExtraTurns + randomAngle

        scope.launch {
            rotation.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            )
            val normalizedAngle = (360f - (rotation.value % 360f)) % 360f
            val sliceAngle = 360f / options.size
            val winnerIndex = (normalizedAngle / sliceAngle).toInt() % options.size
            selectedWinner = options[winnerIndex]
            isSpinning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(modifier = Modifier.size(280.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2 - 12.dp.toPx()
                val n = options.size.coerceAtLeast(1)
                val sweepAngle = 360f / n

                val currentRot = rotation.value

                for (i in 0 until n) {
                    val startAngle = currentRot + i * sweepAngle - 90f
                    val color = colors[i % colors.size]

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(center.x - radius, center.y - radius),
                    )

                    val midAngleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                    val textDist = radius * 0.65f
                    val textX = center.x + textDist * cos(midAngleRad).toFloat()
                    val textY = center.y + textDist * sin(midAngleRad).toFloat()

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            this.color = android.graphics.Color.WHITE
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        val text = if (i < options.size) options[i].take(8) else ""
                        drawText(text, textX, textY + 10f, paint)
                    }
                }

                drawCircle(
                    color = Color.DarkGray,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 6.dp.toPx()),
                )

                drawCircle(color = Color.White, radius = 24.dp.toPx(), center = center)
                drawCircle(color = Color.DarkGray, radius = 24.dp.toPx(), center = center, style = Stroke(4f))
            }

            Canvas(modifier = Modifier.size(280.dp)) {
                val pointerPath = Path().apply {
                    val topCenterX = size.width / 2
                    val topY = 4.dp.toPx()
                    moveTo(topCenterX, topY + 24.dp.toPx())
                    lineTo(topCenterX - 12.dp.toPx(), topY)
                    lineTo(topCenterX + 12.dp.toPx(), topY)
                    close()
                }
                drawPath(pointerPath, color = Color.Red, style = Fill)
            }
        }

        Button(
            onClick = { spin() },
            enabled = !isSpinning && options.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(if (isSpinning) "Spinning..." else "Spin the Wheel")
        }

        if (selectedWinner != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Winner!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        selectedWinner!!,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Wheel Choices (one per line)", style = MaterialTheme.typography.titleSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                OutlinedTextField(
                    value = optionsText,
                    onValueChange = { optionsText = it },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                )
            }
        }
    }
}
