package com.toolbox.labs.touch

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp

data class TouchPointInfo(val id: Int, val pos: Offset, val pressure: Float, val size: Float)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TouchTesterScreen() {
    val activePointers = remember { mutableStateMapOf<Int, TouchPointInfo>() }
    val trails = remember { mutableStateListOf<Offset>() }
    val pointerColors = remember {
        listOf(
            Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047),
            Color(0xFFFB8C00), Color(0xFF8E24AA), Color(0xFF00ACC1),
            Color(0xFFFDD835), Color(0xFFD81B60), Color(0xFF3949AB), Color(0xFF00897B)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .pointerInteropFilter { event ->
                val pointerCount = event.pointerCount
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_MOVE -> {
                        for (i in 0 until pointerCount) {
                            val id = event.getPointerId(i)
                            val offset = Offset(event.getX(i), event.getY(i))
                            val pressure = event.getPressure(i)
                            val size = event.getSize(i)
                            activePointers[id] = TouchPointInfo(id, offset, pressure, size)
                            trails.add(offset)
                            if (trails.size > 200) {
                                trails.removeAt(0)
                            }
                        }
                    }
                    MotionEvent.ACTION_POINTER_UP -> {
                        val actionIndex = event.actionIndex
                        val id = event.getPointerId(actionIndex)
                        activePointers.remove(id)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        activePointers.clear()
                    }
                }
                true
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw trails
            for (p in trails) {
                drawCircle(color = Color.White.copy(alpha = 0.2f), radius = 6.dp.toPx(), center = p)
            }

            // Draw active pointers
            activePointers.values.forEach { pt ->
                val c = pointerColors[pt.id % pointerColors.size]
                // Crosshairs
                drawLine(
                    color = c.copy(alpha = 0.6f),
                    start = Offset(0f, pt.pos.y),
                    end = Offset(size.width, pt.pos.y),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = c.copy(alpha = 0.6f),
                    start = Offset(pt.pos.x, 0f),
                    end = Offset(pt.pos.x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                // Touch circles
                val radius = (40.dp.toPx() * (pt.pressure.coerceIn(0.2f, 1.5f)))
                drawCircle(color = c.copy(alpha = 0.4f), radius = radius, center = pt.pos)
                drawCircle(color = c, radius = 8.dp.toPx(), center = pt.pos)
            }
        }

        // Stats HUD
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "Touch Pointers: ${activePointers.size}",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
            )
            activePointers.values.forEach { pt ->
                Text(
                    text = "ID ${pt.id}: X=${pt.pos.x.toInt()}, Y=${pt.pos.y.toInt()} | P=${"%.2f".format(pt.pressure)}",
                    color = pointerColors[pt.id % pointerColors.size],
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (activePointers.isEmpty()) {
                Text(
                    text = "Touch screen with multiple fingers to test hardware",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
