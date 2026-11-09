package com.toolbox.media.imagesuite

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ImageResizerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var resizedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var origWidth by rememberSaveable { mutableIntStateOf(0) }
    var origHeight by rememberSaveable { mutableIntStateOf(0) }

    var targetWidthStr by rememberSaveable { mutableStateOf("") }
    var targetHeightStr by rememberSaveable { mutableStateOf("") }
    var keepAspect by rememberSaveable { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }

    val pickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            scope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bmp = BitmapFactory.decodeStream(stream)
                        if (bmp != null) {
                            originalBitmap = bmp
                            resizedBitmap = null
                            origWidth = bmp.width
                            origHeight = bmp.height
                            targetWidthStr = bmp.width.toString()
                            targetHeightStr = bmp.height.toString()
                        }
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun applyResize() {
        val bmp = originalBitmap ?: return
        val tw = targetWidthStr.toIntOrNull() ?: return
        val th = targetHeightStr.toIntOrNull() ?: return
        if (tw <= 0 || th <= 0) return

        isProcessing = true
        scope.launch(Dispatchers.IO) {
            val scaled = Bitmap.createScaledBitmap(bmp, tw, th, true)
            withContext(Dispatchers.Main) {
                resizedBitmap = scaled
                isProcessing = false
            }
        }
    }

    fun saveToGallery(bmp: Bitmap) {
        scope.launch(Dispatchers.IO) {
            try {
                val filename = "Resized_${System.currentTimeMillis()}.png"
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Toolbox")
                    }
                }
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, os)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Saved to Pictures/Toolbox", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Button(
            onClick = { pickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (originalBitmap == null) "Select Image to Resize" else "Choose Different Image")
        }

        if (originalBitmap != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Original Size: ${origWidth} × ${origHeight} px", fontWeight = FontWeight.Bold)

                    Text("Presets:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf(75, 50, 25).forEach { pct ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    val nw = (origWidth * pct / 100).coerceAtLeast(1)
                                    val nh = (origHeight * pct / 100).coerceAtLeast(1)
                                    targetWidthStr = nw.toString()
                                    targetHeightStr = nh.toString()
                                },
                                label = { Text("$pct%") },
                            )
                        }
                        listOf("1080p" to (1920 to 1080), "720p" to (1280 to 720), "Square" to (1080 to 1080)).forEach { (name, dims) ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    targetWidthStr = dims.first.toString()
                                    targetHeightStr = dims.second.toString()
                                },
                                label = { Text(name) },
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = targetWidthStr,
                            onValueChange = {
                                targetWidthStr = it
                                val w = it.toIntOrNull()
                                if (keepAspect && w != null && origWidth > 0) {
                                    targetHeightStr = ((w.toDouble() / origWidth) * origHeight).toInt().toString()
                                }
                            },
                            label = { Text("Width (px)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        Text("×", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = targetHeightStr,
                            onValueChange = {
                                targetHeightStr = it
                                val h = it.toIntOrNull()
                                if (keepAspect && h != null && origHeight > 0) {
                                    targetWidthStr = ((h.toDouble() / origHeight) * origWidth).toInt().toString()
                                }
                            },
                            label = { Text("Height (px)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = keepAspect, onCheckedChange = { keepAspect = it })
                        Text("Maintain Aspect Ratio")
                    }

                    Button(
                        onClick = { applyResize() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing,
                    ) {
                        if (isProcessing) CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                        Text("Resize Image")
                    }
                }
            }

            if (resizedBitmap != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text("Preview (${resizedBitmap!!.width} × ${resizedBitmap!!.height})", fontWeight = FontWeight.SemiBold)
                        Image(
                            bitmap = resizedBitmap!!.asImageBitmap(),
                            contentDescription = "Resized preview",
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                        )
                        Button(onClick = { saveToGallery(resizedBitmap!!) }) {
                            Text("Save to Gallery")
                        }
                    }
                }
            }
        }
    }
}
