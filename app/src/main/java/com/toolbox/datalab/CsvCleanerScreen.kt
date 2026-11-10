package com.toolbox.datalab

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun CsvCleanerScreen() {
    val context = LocalContext.current
    var inputCsv by remember { mutableStateOf("Name, Age, City \nAlice, 28, New York\nBob, , Chicago\nAlice, 28, New York\n Charlie , 35, Seattle ") }
    var cleanOutput by remember { mutableStateOf("") }
    var trimWhitespace by remember { mutableStateOf(true) }
    var removeDuplicates by remember { mutableStateOf(true) }
    var dropNulls by remember { mutableStateOf(false) }

    fun clean() {
        val lines = inputCsv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            cleanOutput = ""
            return
        }
        var processed = lines.map { line ->
            if (trimWhitespace) {
                line.split(",").joinToString(",") { it.trim() }
            } else line
        }

        if (removeDuplicates) {
            val header = processed.first()
            val data = processed.drop(1).distinct()
            processed = listOf(header) + data
        }

        if (dropNulls) {
            val header = processed.first()
            val data = processed.drop(1).filter { line ->
                val cols = line.split(",")
                cols.all { it.isNotBlank() }
            }
            processed = listOf(header) + data
        }

        cleanOutput = processed.joinToString("\n")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("CSV Dataset Sanitizer & Deduplicator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Clean dirty tabular data, trim column whitespace, deduplicate records, and filter incomplete entries.")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = trimWhitespace, onClick = { trimWhitespace = !trimWhitespace }, label = { Text("Trim Spaces") })
            FilterChip(selected = removeDuplicates, onClick = { removeDuplicates = !removeDuplicates }, label = { Text("Deduplicate") })
            FilterChip(selected = dropNulls, onClick = { dropNulls = !dropNulls }, label = { Text("Drop Empty Rows") })
        }

        OutlinedTextField(
            value = inputCsv,
            onValueChange = { inputCsv = it },
            label = { Text("Dirty CSV Input") },
            modifier = Modifier.fillMaxWidth().height(160.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
        )

        Button(onClick = { clean() }, modifier = Modifier.fillMaxWidth()) {
            Text("Sanitize & Clean Dataset")
        }

        if (cleanOutput.isNotBlank()) {
            OutlinedTextField(
                value = cleanOutput,
                onValueChange = { cleanOutput = it },
                label = { Text("Cleaned CSV Output") },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
            )

            Button(
                onClick = { copyToClipboard(context, "Clean CSV", cleanOutput) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy Sanitized CSV")
            }
        }
    }
}
