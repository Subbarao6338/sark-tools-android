package com.toolbox.everyday.barcodesuite

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.toolbox.core.ui.copyToClipboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SupportedBarcode(val label: String, val format: BarcodeFormat, val hint: String) {
    CODE_128("Code 128", BarcodeFormat.CODE_128, "Any alphanumeric text"),
    EAN_13("EAN-13", BarcodeFormat.EAN_13, "Exactly 12 or 13 digits"),
    UPC_A("UPC-A", BarcodeFormat.UPC_A, "Exactly 11 or 12 digits"),
    CODE_39("Code 39", BarcodeFormat.CODE_39, "Uppercase A-Z, 0-9, -, ., $, /, +, %, space"),
    QR_CODE("QR Code", BarcodeFormat.QR_CODE, "Any URL or text"),
}

@Composable
fun BarcodeGeneratorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedFormat by rememberSaveable { mutableStateOf(SupportedBarcode.CODE_128) }
    var inputText by rememberSaveable { mutableStateOf("123456789012") }

    val barcodeBitmap = remember(selectedFormat, inputText) {
        if (inputText.isBlank()) null
        else try {
            val width = if (selectedFormat == SupportedBarcode.QR_CODE) 600 else 800
            val height = if (selectedFormat == SupportedBarcode.QR_CODE) 600 else 300
            val bitMatrix = MultiFormatWriter().encode(inputText, selectedFormat.format, width, height)
            bitMatrixToBitmap(bitMatrix)
        } catch (_: Exception) {
            null
        }
    }

    fun saveBarcode() {
        val bmp = barcodeBitmap ?: return
        scope.launch(Dispatchers.IO) {
            try {
                val filename = "Barcode_${selectedFormat.name}_${System.currentTimeMillis()}.png"
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
                        Toast.makeText(context, "Saved barcode to gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save barcode", Toast.LENGTH_SHORT).show()
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select Barcode Format", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SupportedBarcode.values().forEach { fmt ->
                        FilterChip(
                            selected = selectedFormat == fmt,
                            onClick = {
                                selectedFormat = fmt
                                if (fmt == SupportedBarcode.EAN_13 && inputText.length != 13) {
                                    inputText = "1234567890128"
                                } else if (fmt == SupportedBarcode.UPC_A && inputText.length != 12) {
                                    inputText = "123456789012"
                                }
                            },
                            label = { Text(fmt.label) },
                        )
                    }
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Barcode Data") },
                    supportingText = { Text(selectedFormat.hint) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (barcodeBitmap != null) {
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
                    Image(
                        bitmap = barcodeBitmap.asImageBitmap(),
                        contentDescription = "Generated Barcode",
                        modifier = Modifier.fillMaxWidth().height(if (selectedFormat == SupportedBarcode.QR_CODE) 240.dp else 140.dp),
                    )
                    Text(inputText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(onClick = { saveBarcode() }, modifier = Modifier.weight(1f)) {
                            Text("Save Barcode")
                        }
                        OutlinedButton(onClick = { copyToClipboard(context, "Barcode Data", inputText) }, modifier = Modifier.weight(1f)) {
                            Text("Copy Data")
                        }
                    }
                }
            }
        } else if (inputText.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            ) {
                Text(
                    text = "Invalid data for ${selectedFormat.label}.\n${selectedFormat.hint}",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}

fun bitMatrixToBitmap(matrix: BitMatrix): Bitmap {
    val width = matrix.width
    val height = matrix.height
    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (matrix.get(x, y)) Color.BLACK else Color.WHITE
        }
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}
