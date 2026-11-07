package com.toolbox.documents.word

import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.sharing.ImageSharer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun WordEditorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var docTitle by remember { mutableStateOf("Untitled Document") }
    var docContent by remember { mutableStateOf("") }

    val wordCount = remember(docContent) {
        if (docContent.isBlank()) 0 else docContent.trim().split("\\s+".toRegex()).size
    }
    val charCount = remember(docContent) { docContent.length }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Document Title Header
            OutlinedTextField(
                value = docTitle,
                onValueChange = { docTitle = it },
                label = { Text("Document Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
            )

            // Formatting toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = false,
                    onClick = {
                        docContent += "\n# Heading\n"
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatSize, contentDescription = null, modifier = Modifier.height(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Heading")
                        }
                    },
                )
                FilterChip(
                    selected = false,
                    onClick = {
                        docContent += "**Bold Text** "
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatBold, contentDescription = null, modifier = Modifier.height(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Bold")
                        }
                    },
                )
                FilterChip(
                    selected = false,
                    onClick = {
                        docContent += "*Italic Text* "
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatItalic, contentDescription = null, modifier = Modifier.height(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Italic")
                        }
                    },
                )
                FilterChip(
                    selected = false,
                    onClick = {
                        docContent += "\n• List item\n"
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = null, modifier = Modifier.height(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("List")
                        }
                    },
                )
            }

            // Word editor main body
            OutlinedTextField(
                value = docContent,
                onValueChange = { docContent = it },
                placeholder = { Text("Type your document text here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            )

            // Statistics bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Words: $wordCount", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text("Characters: $charCount", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }

            // Export Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val doc = PdfDocument()
                                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
                                val page = doc.startPage(pageInfo)
                                val canvas = page.canvas

                                val paintTitle = Paint().apply {
                                    color = AndroidColor.BLACK
                                    textSize = 22f
                                    isFakeBoldText = true
                                    isAntiAlias = true
                                }
                                val paintBody = Paint().apply {
                                    color = AndroidColor.DKGRAY
                                    textSize = 14f
                                    isAntiAlias = true
                                }

                                canvas.drawText(docTitle, 50f, 60f, paintTitle)
                                var y = 100f
                                val lines = docContent.split("\n")
                                for (line in lines) {
                                    canvas.drawText(line, 50f, y, paintBody)
                                    y += 20f
                                    if (y > 800f) break
                                }

                                doc.finishPage(page)
                                val outDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Toolbox_Documents")
                                outDir.mkdirs()
                                val outFile = File(outDir, "${docTitle.replace(" ", "_")}_${System.currentTimeMillis()}.pdf")
                                FileOutputStream(outFile).use { out ->
                                    doc.writeTo(out)
                                }
                                doc.close()
                                snackbarHostState.showSnackbar("Exported PDF to ${outFile.name}")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Export failed: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Export PDF")
                }

                OutlinedButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val outDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Toolbox_Documents")
                                outDir.mkdirs()
                                val outFile = File(outDir, "${docTitle.replace(" ", "_")}.txt")
                                outFile.writeText("$docTitle\n\n$docContent")
                                snackbarHostState.showSnackbar("Saved Text file: ${outFile.name}")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Save failed: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Save TXT")
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}
