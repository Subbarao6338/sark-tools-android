package com.toolbox.doctools

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import java.io.File
import java.io.FileOutputStream

@Composable
fun CompressPdfScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var originalSize by rememberSaveable { mutableLongStateOf(0L) }
    var pageCount by rememberSaveable { mutableIntStateOf(0) }
    var compressedSize by rememberSaveable { mutableLongStateOf(0L) }
    var scaleQuality by rememberSaveable { mutableFloatStateOf(60f) }
    var isCompressing by remember { mutableStateOf(false) }
    var tempInputFile by remember { mutableStateOf<File?>(null) }
    var compressedOutputFile by remember { mutableStateOf<File?>(null) }

    val pickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            compressedSize = 0L
            compressedOutputFile = null
            scope.launch(Dispatchers.IO) {
                try {
                    val temp = File(context.cacheDir, "input_compress.pdf")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(temp).use { output ->
                            input.copyTo(output)
                        }
                    }
                    tempInputFile = temp
                    originalSize = temp.length()

                    val pfd = ParcelFileDescriptor.open(temp, ParcelFileDescriptor.MODE_READ_ONLY)
                    val renderer = PdfRenderer(pfd)
                    pageCount = renderer.pageCount
                    renderer.close()
                    pfd.close()
                } catch (_: Exception) {}
            }
        }
    }

    fun compress() {
        val input = tempInputFile ?: return
        isCompressing = true
        scope.launch(Dispatchers.IO) {
            try {
                val pfd = ParcelFileDescriptor.open(input, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(pfd)

                val doc = PdfDocument()
                val scale = (scaleQuality / 100f).coerceIn(0.3f, 1.0f)

                for (i in 0 until renderer.pageCount) {
                    val page = renderer.openPage(i)
                    val scaledW = (page.width * scale).toInt().coerceAtLeast(100)
                    val scaledH = (page.height * scale).toInt().coerceAtLeast(100)

                    val bmp = Bitmap.createBitmap(scaledW, scaledH, Bitmap.Config.RGB_565)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                    page.close()

                    val pageInfo = PdfDocument.PageInfo.Builder(scaledW, scaledH, i + 1).create()
                    val pdfPage = doc.startPage(pageInfo)
                    val canvas = pdfPage.canvas
                    canvas.drawBitmap(bmp, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG))
                    doc.finishPage(pdfPage)
                    bmp.recycle()
                }
                renderer.close()
                pfd.close()

                val outFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.pdf")
                FileOutputStream(outFile).use { os ->
                    doc.writeTo(os)
                }
                doc.close()

                compressedOutputFile = outFile
                withContext(Dispatchers.Main) {
                    compressedSize = outFile.length()
                    isCompressing = false
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    isCompressing = false
                    Toast.makeText(context, "Error compressing PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun saveCompressedToDownloads() {
        val file = compressedOutputFile ?: return
        scope.launch(Dispatchers.IO) {
            try {
                val filename = "Compressed_${System.currentTimeMillis()}.pdf"
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, filename)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Downloads.RELATIVE_PATH, "Download/Toolbox")
                    }
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        file.inputStream().use { input -> input.copyTo(output) }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Saved to Downloads/Toolbox", Toast.LENGTH_SHORT).show()
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
            onClick = { pickerLauncher.launch("application/pdf") },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (tempInputFile == null) "Select PDF to Compress" else "Choose Different PDF")
        }

        if (tempInputFile != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Original Info", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("Pages: $pageCount")
                    Text("Original Size: ${originalSize / 1024} KB")

                    Text("Compression Level: ${scaleQuality.toInt()}%")
                    Slider(
                        value = scaleQuality,
                        onValueChange = { scaleQuality = it },
                        valueRange = 30f..90f,
                        steps = 5,
                    )

                    Button(
                        onClick = { compress() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isCompressing,
                    ) {
                        if (isCompressing) CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                        Text("Compress PDF")
                    }
                }
            }

            if (compressedSize > 0L) {
                val reduction = if (originalSize > 0) ((originalSize - compressedSize).toDouble() / originalSize * 100).toInt() else 0
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
                        Text("Compressed Successfully!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(
                            "New Size: ${compressedSize / 1024} KB (Saved $reduction%)",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Button(onClick = { saveCompressedToDownloads() }) {
                            Text("Save to Downloads")
                        }
                    }
                }
            }
        }
    }
}
