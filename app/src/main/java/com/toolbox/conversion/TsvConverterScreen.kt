package com.toolbox.conversion

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun TsvConverterScreen() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("ID\tProduct\tPrice\n1\tLaptop\t999\n2\tPhone\t499") }
    var output by remember { mutableStateOf("") }

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
                Text("TSV ↔ CSV Format Transcoder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Convert tab-separated tables directly into standard comma-separated format and vice versa.")
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Input TSV / CSV Content") },
            modifier = Modifier.fillMaxWidth().height(160.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val lines = input.lines()
                output = lines.joinToString("\n") { it.replace("\t", ",") }
            }) {
                Text("TSV → CSV")
            }
            OutlinedButton(onClick = {
                val lines = input.lines()
                output = lines.joinToString("\n") { it.replace(",", "\t") }
            }) {
                Text("CSV → TSV")
            }
        }

        if (output.isNotBlank()) {
            OutlinedTextField(
                value = output,
                onValueChange = { output = it },
                label = { Text("Transcoded Output") },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
            )

            Button(
                onClick = { copyToClipboard(context, "Transcoded Data", output) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy Output")
            }
        }
    }
}
