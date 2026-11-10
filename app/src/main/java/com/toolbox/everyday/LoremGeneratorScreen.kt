package com.toolbox.everyday

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun LoremGeneratorScreen() {
    val context = LocalContext.current
    var unitType by remember { mutableStateOf("Paragraphs") }
    var count by remember { mutableIntStateOf(3) }
    var generatedText by remember { mutableStateOf("") }

    val sampleWords = listOf(
        "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
        "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
        "magna", "aliqua", "enim", "ad", "minim", "veniam", "quis", "nostrud",
        "exercitation", "ullamco", "laboris", "nisi", "ut", "aliquip", "ex", "ea",
        "commodo", "consequat", "duis", "aute", "irure", "in", "reprehenderit"
    )

    fun generate() {
        if (unitType == "Words") {
            generatedText = (1..count.coerceIn(1, 500)).joinToString(" ") { sampleWords[it % sampleWords.size] }
        } else {
            val paragraphs = (1..count.coerceIn(1, 20)).map {
                (1..30).joinToString(" ") { sampleWords[it % sampleWords.size] }.capitalize() + "."
            }
            generatedText = paragraphs.joinToString("\n\n")
        }
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
                Text("Lorem Ipsum Dummy Text Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Generate offline placeholder text for design, prototyping, and typography testing.")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = unitType == "Paragraphs", onClick = { unitType = "Paragraphs" }, label = { Text("Paragraphs") })
            FilterChip(selected = unitType == "Words", onClick = { unitType = "Words" }, label = { Text("Words") })
        }

        OutlinedTextField(
            value = count.toString(),
            onValueChange = { count = it.toIntOrNull() ?: 1 },
            label = { Text("Count ($unitType)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { generate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Generate Placeholder Text")
        }

        if (generatedText.isNotBlank()) {
            OutlinedTextField(
                value = generatedText,
                onValueChange = { generatedText = it },
                label = { Text("Generated Result") },
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )

            Button(
                onClick = { copyToClipboard(context, "Lorem Text", generatedText) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy to Clipboard")
            }
        }
    }
}
