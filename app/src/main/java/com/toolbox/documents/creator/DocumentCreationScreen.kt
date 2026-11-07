package com.toolbox.documents.creator

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

enum class DocTemplate(val title: String) {
    Invoice("Invoice"),
    Resume("Resume / CV"),
    MeetingNotes("Meeting Notes"),
    Agreement("Simple Agreement")
}

@Composable
fun DocumentCreationScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTemplate by remember { mutableStateOf(DocTemplate.Invoice) }

    // Dynamic Fields
    var field1 by remember { mutableStateOf("Client Name / Recipient") }
    var field2 by remember { mutableStateOf("Item Description / Service") }
    var field3 by remember { mutableStateOf("150.00") }
    var notes by remember { mutableStateOf("Payment due within 30 days. Thank you for your business.") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Select Template", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DocTemplate.values().forEach { tmpl ->
                    FilterChip(
                        selected = selectedTemplate == tmpl,
                        onClick = {
                            selectedTemplate = tmpl
                            when (tmpl) {
                                DocTemplate.Invoice -> {
                                    field1 = "Acme Corp"
                                    field2 = "Mobile App Design & Implementation"
                                    field3 = "$1,200.00"
                                    notes = "Payment due in 15 days via bank transfer."
                                }
                                DocTemplate.Resume -> {
                                    field1 = "Alex Mercer — Software Engineer"
                                    field2 = "5+ years Kotlin & Android development"
                                    field3 = "B.S. in Computer Science"
                                    notes = "Contact: alex@example.com | Portfolio: https://github.com"
                                }
                                DocTemplate.MeetingNotes -> {
                                    field1 = "Weekly Engineering Sync"
                                    field2 = "Reviewed release pipeline & tool catalog"
                                    field3 = "Attendees: Engineering Team"
                                    notes = "Action Items: Complete test runs and finalize pull request."
                                }
                                DocTemplate.Agreement -> {
                                    field1 = "Service Level Agreement"
                                    field2 = "Parties: Provider and Client"
                                    field3 = "Effective Date: Immediate"
                                    notes = "Both parties agree to standard terms and conditions."
                                }
                            }
                        },
                        label = { Text(tmpl.title) },
                    )
                }
            }

            // Fillable details card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Document Details", style = MaterialTheme.typography.titleSmall)

                    OutlinedTextField(
                        value = field1,
                        onValueChange = { field1 = it },
                        label = {
                            Text(
                                when (selectedTemplate) {
                                    DocTemplate.Invoice -> "Client / Recipient"
                                    DocTemplate.Resume -> "Name & Title"
                                    DocTemplate.MeetingNotes -> "Meeting Subject"
                                    DocTemplate.Agreement -> "Agreement Title"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = field2,
                        onValueChange = { field2 = it },
                        label = {
                            Text(
                                when (selectedTemplate) {
                                    DocTemplate.Invoice -> "Description / Line Item"
                                    DocTemplate.Resume -> "Key Qualifications / Experience"
                                    DocTemplate.MeetingNotes -> "Discussion Summary"
                                    DocTemplate.Agreement -> "Scope of Work"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = field3,
                        onValueChange = { field3 = it },
                        label = {
                            Text(
                                when (selectedTemplate) {
                                    DocTemplate.Invoice -> "Total Amount"
                                    DocTemplate.Resume -> "Education / Credentials"
                                    DocTemplate.MeetingNotes -> "Participants"
                                    DocTemplate.Agreement -> "Terms / Duration"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Terms / Additional Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                    )
                }
            }

            // Generate Button
            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        try {
                            val doc = PdfDocument()
                            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
                            val page = doc.startPage(pageInfo)
                            val canvas = page.canvas

                            val paintHeader = Paint().apply {
                                color = AndroidColor.rgb(0, 137, 123)
                                textSize = 24f
                                isFakeBoldText = true
                                isAntiAlias = true
                            }
                            val paintSub = Paint().apply {
                                color = AndroidColor.BLACK
                                textSize = 16f
                                isFakeBoldText = true
                                isAntiAlias = true
                            }
                            val paintText = Paint().apply {
                                color = AndroidColor.DKGRAY
                                textSize = 13f
                                isAntiAlias = true
                            }

                            // Header
                            canvas.drawText(selectedTemplate.title.uppercase(), 50f, 60f, paintHeader)

                            // Content block
                            canvas.drawText("Primary Entity / Subject:", 50f, 110f, paintSub)
                            canvas.drawText(field1, 50f, 130f, paintText)

                            canvas.drawText("Details / Description:", 50f, 170f, paintSub)
                            canvas.drawText(field2, 50f, 190f, paintText)

                            canvas.drawText("Specification / Value:", 50f, 230f, paintSub)
                            canvas.drawText(field3, 50f, 250f, paintText)

                            canvas.drawText("Terms & Conditions / Notes:", 50f, 290f, paintSub)
                            var curY = 310f
                            for (line in notes.split("\n")) {
                                canvas.drawText(line, 50f, curY, paintText)
                                curY += 18f
                            }

                            doc.finishPage(page)
                            val outDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Toolbox_CreatedDocs")
                            outDir.mkdirs()
                            val outFile = File(outDir, "${selectedTemplate.name}_${System.currentTimeMillis()}.pdf")
                            FileOutputStream(outFile).use { out ->
                                doc.writeTo(out)
                            }
                            doc.close()
                            snackbarHostState.showSnackbar("Document generated: ${outFile.name}")
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Error generating document: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.PostAdd, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Generate PDF Document")
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}
