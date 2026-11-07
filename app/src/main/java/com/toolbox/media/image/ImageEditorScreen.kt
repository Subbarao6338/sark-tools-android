package com.toolbox.media.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.toolbox.core.media.MediaStoreWriter
import com.toolbox.core.sharing.ImageSharer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class ImageFilterMode {
    None, Grayscale, Sepia, Invert
}

data class DrawPathPoint(val start: Offset, val end: Offset, val color: Color, val strokeWidth: Float)

@Composable
fun ImageEditorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var brightness by remember { mutableFloatStateOf(0f) } // -100 to 100
    var contrast by remember { mutableFloatStateOf(1f) }   // 0.5 to 2.0
    var activeFilter by remember { mutableStateOf(ImageFilterMode.None) }
    var isDrawingMode by remember { mutableStateOf(false) }
    var brushColor by remember { mutableStateOf(Color.Red) }
    val drawPaths = remember { mutableStateListOf<DrawPathPoint>() }
    var currentDragStart by remember { mutableStateOf<Offset?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val options = BitmapFactory.Options().apply { inSampleSize = 1 }
                        val bmp = BitmapFactory.decodeStream(stream, null, options)
                        withContext(Dispatchers.Main) {
                            originalBitmap = bmp
                            rotationAngle = 0f
                            brightness = 0f
                            contrast = 1f
                            activeFilter = ImageFilterMode.None
                            drawPaths.clear()
                        }
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Failed to load image: ${e.message}")
                }
            }
        }
    }

    fun buildProcessedBitmap(): Bitmap? {
        val src = originalBitmap ?: return null
        val matrix = Matrix().apply { postRotate(rotationAngle) }
        val rotated = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        val result = Bitmap.createBitmap(rotated.width, rotated.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        val cm = ColorMatrix()
        // Brightness & Contrast
        val scale = contrast
        val translate = brightness
        cm.set(floatArrayOf(
            scale, 0f, 0f, 0f, translate,
            0f, scale, 0f, 0f, translate,
            0f, 0f, scale, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        ))

        when (activeFilter) {
            ImageFilterMode.Grayscale -> {
                val gray = ColorMatrix().apply { setSaturation(0f) }
                cm.postConcat(gray)
            }
            ImageFilterMode.Sepia -> {
                val sepia = ColorMatrix(floatArrayOf(
                    0.393f, 0.769f, 0.189f, 0f, 0f,
                    0.349f, 0.686f, 0.168f, 0f, 0f,
                    0.272f, 0.534f, 0.131f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(sepia)
            }
            ImageFilterMode.Invert -> {
                val invert = ColorMatrix(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(invert)
            }
            ImageFilterMode.None -> {}
        }

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(cm)
            isAntiAlias = true
        }
        canvas.drawBitmap(rotated, 0f, 0f, paint)

        // Draw overlay paths
        val overlayPaint = Paint().apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
        val scaleX = rotated.width.toFloat()
        val scaleY = rotated.height.toFloat()
        for (p in drawPaths) {
            overlayPaint.color = android.graphics.Color.argb(
                (p.color.alpha * 255).toInt(),
                (p.color.red * 255).toInt(),
                (p.color.green * 255).toInt(),
                (p.color.blue * 255).toInt()
            )
            overlayPaint.strokeWidth = p.strokeWidth * (scaleX / 300f).coerceAtLeast(1f)
            canvas.drawLine(p.start.x * scaleX, p.start.y * scaleY, p.end.x * scaleX, p.end.y * scaleY, overlayPaint)
        }
        return result
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (originalBitmap == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                "Select an image to start editing",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Button(onClick = { photoPickerLauncher.launch("image/*") }) {
                                Text("Choose Photo")
                            }
                        }
                    }
                }
            } else {
                val processed = remember(originalBitmap, rotationAngle, brightness, contrast, activeFilter, drawPaths.size) {
                    buildProcessedBitmap()
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (processed != null) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(isDrawingMode) {
                                        if (isDrawingMode) {
                                            detectDragGestures(
                                                onDragStart = { offset ->
                                                    currentDragStart = offset
                                                },
                                                onDrag = { change, _ ->
                                                    change.consume()
                                                    val start = currentDragStart
                                                    if (start != null) {
                                                        val normStart = Offset(start.x / size.width, start.y / size.height)
                                                        val normEnd = Offset(change.position.x / size.width, change.position.y / size.height)
                                                        drawPaths.add(DrawPathPoint(normStart, normEnd, brushColor, 8f))
                                                        currentDragStart = change.position
                                                    }
                                                },
                                                onDragEnd = { currentDragStart = null },
                                                onDragCancel = { currentDragStart = null },
                                            )
                                        }
                                    }
                            ) {
                                drawImage(
                                    image = processed.asImageBitmap(),
                                    dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), size.height.toInt()),
                                )
                                for (p in drawPaths) {
                                    drawLine(
                                        color = p.color,
                                        start = Offset(p.start.x * size.width, p.start.y * size.height),
                                        end = Offset(p.end.x * size.width, p.end.y * size.height),
                                        strokeWidth = p.strokeWidth,
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Change")
                    }
                    Button(
                        onClick = {
                            rotationAngle = (rotationAngle + 90f) % 360f
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.RotateRight, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Rotate")
                    }
                }

                // Filters
                Text("Filters", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ImageFilterMode.values().forEach { mode ->
                        FilterChip(
                            selected = activeFilter == mode,
                            onClick = { activeFilter = mode },
                            label = { Text(mode.name) },
                        )
                    }
                }

                // Adjustments: Brightness & Contrast
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Brightness: ${brightness.toInt()}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Slider(
                            value = brightness,
                            onValueChange = { brightness = it },
                            valueRange = -100f..100f,
                        )

                        Text("Contrast: ${"%.1f".format(contrast)}x", style = MaterialTheme.typography.bodyMedium)
                        Slider(
                            value = contrast,
                            onValueChange = { contrast = it },
                            valueRange = 0.5f..2.5f,
                        )
                    }
                }

                // Drawing Brush Mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Brush, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text("Drawing / Annotation", style = MaterialTheme.typography.bodyMedium)
                            }
                            FilterChip(
                                selected = isDrawingMode,
                                onClick = { isDrawingMode = !isDrawingMode },
                                label = { Text(if (isDrawingMode) "Active" else "Off") },
                            )
                        }
                        if (isDrawingMode) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.White, Color.Black).forEach { c ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(c, CircleShape)
                                            .clip(CircleShape)
                                            .pointerInput(c) {
                                                brushColor = c
                                            },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (brushColor == c) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (c == Color.White) Color.Black else Color.White,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.weight(1f))
                                OutlinedButton(onClick = { drawPaths.clear() }) {
                                    Text("Clear")
                                }
                            }
                        }
                    }
                }

                // Export Actions: Save & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                val bmp = buildProcessedBitmap()
                                if (bmp != null) {
                                    val res = MediaStoreWriter.saveJpeg(context, bmp, "Toolbox_Edited", "IMG_EDIT", 95)
                                    snackbarHostState.showSnackbar("Saved image to ${res.displayPath}")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Save")
                    }
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                val bmp = buildProcessedBitmap()
                                if (bmp != null) {
                                    ImageSharer.shareBitmap(context, bmp, "shared_edited_image.png")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Share")
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}
