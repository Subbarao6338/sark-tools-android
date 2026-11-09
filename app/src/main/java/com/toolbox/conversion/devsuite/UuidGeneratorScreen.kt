package com.toolbox.conversion.devsuite

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import java.util.UUID

@Composable
fun UuidGeneratorScreen() {
    val context = LocalContext.current
    var count by rememberSaveable { mutableFloatStateOf(5f) }
    var uppercase by rememberSaveable { mutableStateOf(false) }
    var includeHyphens by rememberSaveable { mutableStateOf(true) }
    val generatedUuids = remember { mutableStateListOf<String>() }

    fun generate() {
        generatedUuids.clear()
        for (i in 1..count.toInt()) {
            var u = UUID.randomUUID().toString()
            if (!includeHyphens) u = u.replace("-", "")
            if (uppercase) u = u.uppercase()
            generatedUuids.add(u)
        }
    }

    if (generatedUuids.isEmpty()) {
        generate()
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
                Text("UUID Options", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("Count: ${count.toInt()}")
                Slider(
                    value = count,
                    onValueChange = { count = it },
                    valueRange = 1f..50f,
                    steps = 48,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uppercase, onCheckedChange = { uppercase = it })
                        Text("Uppercase")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includeHyphens, onCheckedChange = { includeHyphens = it })
                        Text("Hyphens")
                    }
                }

                Button(
                    onClick = { generate() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Generate UUIDs")
                }
            }
        }

        val allText = generatedUuids.joinToString("\n")
        OutlinedTextField(
            value = allText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Generated UUIDs (Version 4)") },
            modifier = Modifier.fillMaxWidth().height(260.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace),
        )

        OutlinedButton(
            onClick = { copyToClipboard(context, "UUIDs", allText) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Copy All UUIDs")
        }
    }
}
