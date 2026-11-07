package com.toolbox.documents.pdf

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun PdfEditorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    val pageBitmaps = remember { mutableStateListOf<Bitmap>() }
    var selectedPageIndex by remember { mutableIntStateOf(0) }
    var watermarkText by remember { mutableStateOf("") }
    var isAddingWatermark by remember { mutableStateOf(false) }

    fun loadPdf(uri: Uri) {
        scope.launch(Dispatchers.IO) {
            try {
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                if (parcelFileDescriptor != null) {
                    val renderer = PdfRenderer(parcelFileDescriptor)
                    val bitmaps = mutableListOf<Bitmap>()
                    val pageCount = renderer.pageCount
                    for (i in 0 until pageCount) {
                        val page = renderer.openPage(i)
                        val bmp = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.close()
                        bitmaps.add(bmp)
                    }
                    renderer.close()
                    parcelFileDescriptor.close()
                    withContext(Dispatchers.Main) {
                        pageBitmaps.clear()
                        pageBitmaps.addAll(bitmaps)
                        selectedPageIndex = 0
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    snackbarHostState.showSnackbar("Failed to open PDF: ${e.message}")
                }
            }
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            pdfUri = uri
            loadPdf(uri)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (pdfUri == null || pageBitmaps.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text("Select a PDF to view and edit", style = MaterialTheme.typography.titleMedium)
                            Button(onClick = { pdfPickerLauncher.launch("application/pdf") }) {
                                Text("Choose PDF Document")
                            }
                        }
                    }
                }
            } else {
                // Active Page Preview
                val currentBmp = pageBitmaps.getOrNull(selectedPageIndex)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.3f)),
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (currentBmp != null) {
                            Image(
                                bitmap = currentBmp.asImageBitmap(),
                                contentDescription = "Page ${selectedPageIndex + 1}",
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        if (watermarkText.isNotBlank()) {
                            Text(
                                text = watermarkText,
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.Red.copy(alpha = 0.35f),
                            )
                        }
                    }
                }

                // Page management controls: Delete page, change order
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Page ${selectedPageIndex + 1} of ${pageBitmaps.size}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                if (pageBitmaps.size > 1) {
                                    pageBitmaps.removeAt(selectedPageIndex)
                                    selectedPageIndex = selectedPageIndex.coerceAtMost(pageBitmaps.size - 1)
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar("Cannot remove only page") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Page", tint = MaterialTheme.colorScheme.error)
                        }
                        OutlinedButton(onClick = { pdfPickerLauncher.launch("application/pdf") }) {
                            Text("Change PDF")
                        }
                    }
                }

                // Page Thumbnails Strip
                Text("Pages", style = MaterialTheme.typography.titleSmall)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(pageBitmaps) { idx, bmp ->
                        Box(
                            modifier = Modifier
                                .size(70.dp, 100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selectedPageIndex == idx) 3.dp else 1.dp,
                                    color = if (selectedPageIndex == idx) MaterialTheme.colorScheme.primary else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedPageIndex = idx },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Thumb ${idx + 1}",
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }

                // Annotate / Watermark
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TextFields, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Watermark / Stamp", style = MaterialTheme.typography.titleSmall)
                        }
                        OutlinedTextField(
                            value = watermarkText,
                            onValueChange = { watermarkText = it },
                            placeholder = { Text("e.g. CONFIDENTIAL, DRAFT, APPROVED") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                }

                // Export & Save PDF
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val doc = PdfDocument()
                                for ((i, bmp) in pageBitmaps.withIndex()) {
                                    val pageInfo = PdfDocument.PageInfo.Builder(bmp.width, bmp.height, i + 1).create()
                                    val page = doc.startPage(pageInfo)
                                    val canvas = page.canvas
                                    canvas.drawBitmap(bmp, 0f, 0f, null)

                                    if (watermarkText.isNotBlank()) {
                                        val p = Paint().apply {
                                            color = AndroidColor.RED
                                            alpha = 80
                                            textSize = (bmp.width / 10f).coerceAtLeast(36f)
                                            textAlign = Paint.Align.CENTER
                                            isAntiAlias = true
                                        }
                                        canvas.save()
                                        canvas.rotate(-45f, bmp.width / 2f, bmp.height / 2f)
                                        canvas.drawText(watermarkText, bmp.width / 2f, bmp.height / 2f, p)
                                        canvas.restore()
                                    }
                                    doc.finishPage(page)
                                }

                                val outDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Toolbox_PDF")
                                outDir.mkdirs()
                                val outFile = File(outDir, "edited_${System.currentTimeMillis()}.pdf")
                                FileOutputStream(outFile).use { out ->
                                    doc.writeTo(out)
                                }
                                doc.close()
                                snackbarHostState.showSnackbar("PDF exported to ${outFile.name}")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error exporting PDF: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Export Modified PDF")
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}
