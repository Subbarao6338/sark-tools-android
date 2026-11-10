package com.toolbox.everyday

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TextDiffViewerScreen() {
    var textOriginal by remember { mutableStateOf("alpha = 10\nbeta = 20\ngamma = 30\nstatus = ok") }
    var textModified by remember { mutableStateOf("alpha = 10\nbeta = 25\ngamma = 30\nstatus = success\nmode = fast") }
    var diffLines by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    fun computeDiff() {
        val orig = textOriginal.lines()
        val mod = textModified.lines()
        val maxLen = kotlin.math.max(orig.size, mod.size)
        val result = mutableListOf<Pair<String, String>>()

        for (i in 0 until maxLen) {
            val o = orig.getOrNull(i)
            val m = mod.getOrNull(i)
            if (o == m && o != null) {
                result.add(" " to o)
            } else {
                if (o != null) result.add("-" to o)
                if (m != null) result.add("+" to m)
            }
        }
        diffLines = result
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
                Text("Text & Configuration Diff Viewer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Inspect line-by-line differences, additions (+), and removals (-) between two text blocks.")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = textOriginal,
                onValueChange = { textOriginal = it },
                label = { Text("Original") },
                modifier = Modifier.weight(1f).height(140.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
            )
            OutlinedTextField(
                value = textModified,
                onValueChange = { textModified = it },
                label = { Text("Modified") },
                modifier = Modifier.weight(1f).height(140.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
            )
        }

        Button(onClick = { computeDiff() }, modifier = Modifier.fillMaxWidth()) {
            Text("Compare Text Blocks")
        }

        if (diffLines.isNotEmpty()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    diffLines.forEach { (prefix, content) ->
                        val color = when (prefix) {
                            "+" -> MaterialTheme.colorScheme.primary
                            "-" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Text(
                            "$prefix $content",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (prefix != " ") FontWeight.Bold else FontWeight.Normal,
                            color = color
                        )
                    }
                }
            }
        }
    }
}
