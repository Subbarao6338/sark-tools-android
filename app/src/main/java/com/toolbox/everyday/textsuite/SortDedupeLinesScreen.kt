package com.toolbox.everyday.textsuite

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun SortDedupeLinesScreen() {
    val context = LocalContext.current
    var input by rememberSaveable { mutableStateOf("Banana\nApple\nOrange\nApple\n100\n20\nBanana\nCherry") }
    var output by rememberSaveable { mutableStateOf("") }
    var trimSpaces by rememberSaveable { mutableStateOf(true) }
    var ignoreCase by rememberSaveable { mutableStateOf(true) }

    fun processLines(action: String) {
        var lines = input.lines()
        if (trimSpaces) lines = lines.map { it.trim() }

        when (action) {
            "Dedupe" -> {
                val seen = mutableSetOf<String>()
                val res = mutableListOf<String>()
                for (line in lines) {
                    val key = if (ignoreCase) line.lowercase() else line
                    if (seen.add(key)) res.add(line)
                }
                output = res.joinToString("\n")
            }
            "A-Z" -> {
                output = if (ignoreCase) {
                    lines.sortedWith(String.CASE_INSENSITIVE_ORDER).joinToString("\n")
                } else {
                    lines.sorted().joinToString("\n")
                }
            }
            "Z-A" -> {
                output = if (ignoreCase) {
                    lines.sortedWith(String.CASE_INSENSITIVE_ORDER.reversed()).joinToString("\n")
                } else {
                    lines.sortedDescending().joinToString("\n")
                }
            }
            "Numeric" -> {
                output = lines.sortedWith(Comparator { a, b ->
                    val numA = a.toDoubleOrNull()
                    val numB = b.toDoubleOrNull()
                    if (numA != null && numB != null) numA.compareTo(numB)
                    else a.compareTo(b)
                }).joinToString("\n")
            }
            "By Length" -> {
                output = lines.sortedBy { it.length }.joinToString("\n")
            }
            "Reverse" -> {
                output = lines.reversed().joinToString("\n")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Original Lines (${input.lines().size} lines)") },
            modifier = Modifier.fillMaxWidth().height(160.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = trimSpaces,
                onClick = { trimSpaces = !trimSpaces },
                label = { Text("Trim Spaces") },
            )
            FilterChip(
                selected = ignoreCase,
                onClick = { ignoreCase = !ignoreCase },
                label = { Text("Ignore Case") },
            )
        }

        Text("Actions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("Dedupe", "A-Z", "Z-A", "Numeric", "By Length", "Reverse").forEach { act ->
                Button(onClick = { processLines(act) }) {
                    Text(act)
                }
            }
        }

        if (output.isNotEmpty()) {
            OutlinedTextField(
                value = output,
                onValueChange = { output = it },
                label = { Text("Processed Output (${output.lines().size} lines)") },
                modifier = Modifier.fillMaxWidth().height(180.dp),
            )

            OutlinedButton(
                onClick = { copyToClipboard(context, "Processed Lines", output) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Copy result") }
        }
    }
}
