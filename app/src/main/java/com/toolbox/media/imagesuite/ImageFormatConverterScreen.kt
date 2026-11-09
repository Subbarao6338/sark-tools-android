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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@Composable
fun ImageFormatConverterScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var originalSize by rememberSaveable { mutableLongStateOf(0L) }
    var selectedFormat by rememberSaveable { mutableStateOf("JPEG") }
    var quality by rememberSaveable { mutableFloatStateOf(85f) }
    var convertedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isConverting by remember { mutableStateOf(false) }

    val pickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bytes = stream.readBytes()
                        originalSize = bytes.size.toLong()
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        originalBitmap = bmp
                        convertedBytes = null
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun convertImage() {
        val bmp = originalBitmap ?: return
        isConverting = true
        scope.launch(Dispatchers.IO) {
            val baos = ByteArrayOutputStream()
            val format = when (selectedFormat) {
                "PNG" -> Bitmap.CompressFormat.PNG
                "WEBP" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP
                else -> Bitmap.CompressFormat.JPEG
            }
            bmp.compress(format, quality.toInt(), baos)
            val bytes = baos.toByteArray()
            withContext(Dispatchers.Main) {
                convertedBytes = bytes
                isConverting = false
            }
        }
    }

    fun saveConverted() {
        val bytes = convertedBytes ?: return
        scope.launch(Dispatchers.IO) {
            try {
                val ext = when (selectedFormat) {
                    "PNG" -> "png"
                    "WEBP" -> "webp"
                    else -> "jpg"
                }
                val mime = when (selectedFormat) {
                    "PNG" -> "image/png"
                    "WEBP" -> "image/webp"
                    else -> "image/jpeg"
                }
                val filename = "Converted_${System.currentTimeMillis()}.$ext"
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, mime)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Toolbox")
                    }
                }
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Saved as $filename", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
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
            Text(if (originalBitmap == null) "Select Image" else "Choose Different Image")
        }

        if (originalBitmap != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Target Format", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf("JPEG", "PNG", "WEBP").forEach { fmt ->
                            FilterChip(
                                selected = selectedFormat == fmt,
                                onClick = { selectedFormat = fmt },
                                label = { Text(fmt) },
                            )
                        }
                    }

                    if (selectedFormat != "PNG") {
                        Text("Quality: ${quality.toInt()}%")
                        Slider(
                            value = quality,
                            onValueChange = { quality = it },
                            valueRange = 10f..100f,
                            steps = 17,
                        )
                    }

                    Button(
                        onClick = { convertImage() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isConverting,
                    ) {
                        if (isConverting) CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                        Text("Convert to $selectedFormat")
                    }
                }
            }

            if (convertedBytes != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Conversion Complete", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(
                            "Original: ${originalSize / 1024} KB  →  Converted: ${convertedBytes!!.size / 1024} KB",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Button(onClick = { saveConverted() }) {
                            Text("Save Converted Image")
                        }
                    }
                }
            }
        }
    }
}
