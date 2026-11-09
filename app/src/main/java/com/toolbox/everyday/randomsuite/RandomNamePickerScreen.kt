package com.toolbox.everyday.randomsuite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun RandomNamePickerScreen() {
    val context = LocalContext.current
    var listInput by rememberSaveable { mutableStateOf("Alice\nBob\nCharlie\nDiana\nEthan\nFiona\nGeorge") }
    var pickCountStr by rememberSaveable { mutableStateOf("1") }
    var removePicked by rememberSaveable { mutableStateOf(false) }

    val pickedResults = remember { mutableStateListOf<String>() }

    fun pick() {
        val lines = listInput.lines().map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
        if (lines.isEmpty()) return

        val count = (pickCountStr.toIntOrNull() ?: 1).coerceIn(1, lines.size)
        lines.shuffle()
        val chosen = lines.take(count)

        pickedResults.clear()
        pickedResults.addAll(chosen)

        if (removePicked) {
            val remaining = lines.drop(count)
            listInput = remaining.joinToString("\n")
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
                Text("Enter Names / Items (one per line)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = listInput,
                    onValueChange = { listInput = it },
                    label = { Text("List of Items") },
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = pickCountStr,
                        onValueChange = { pickCountStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Pick Count") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(checked = removePicked, onCheckedChange = { removePicked = it })
                        Text("Remove drawn", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Button(
                    onClick = { pick() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Pick Random Item(s)")
                }
            }
        }

        if (pickedResults.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Selected Winner(s)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    pickedResults.forEach { item ->
                        Text(
                            item,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    OutlinedButton(
                        onClick = { copyToClipboard(context, "Picked Items", pickedResults.joinToString(", ")) },
                    ) {
                        Text("Copy Winner(s)")
                    }
                }
            }
        }
    }
}
