package com.toolbox.conversion.devsuite

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun JsonFormatterScreen() {
    val context = LocalContext.current
    var inputJson by rememberSaveable { mutableStateOf("{\"name\":\"Sark Tools\",\"version\":1,\"features\":[\"offline\",\"privacy\",\"fast\"],\"details\":{\"minSdk\":24,\"architecture\":\"arm64-v8a\"}}") }
    var outputJson by rememberSaveable { mutableStateOf("") }
    var indentSpaces by rememberSaveable { mutableIntStateOf(2) }
    var validationStatus by rememberSaveable { mutableStateOf<String?>(null) }

    fun format(indent: Int) {
        if (inputJson.isBlank()) {
            outputJson = ""
            validationStatus = null
            return
        }
        try {
            val trimmed = inputJson.trim()
            val formatted = if (trimmed.startsWith("[")) {
                val arr = JSONArray(trimmed)
                arr.toString(indent)
            } else {
                val obj = JSONObject(trimmed)
                obj.toString(indent)
            }
            outputJson = formatted
            validationStatus = "Valid JSON"
        } catch (e: Exception) {
            validationStatus = "Invalid JSON: ${e.message}"
        }
    }

    fun minify() {
        if (inputJson.isBlank()) return
        try {
            val trimmed = inputJson.trim()
            outputJson = if (trimmed.startsWith("[")) {
                JSONArray(trimmed).toString()
            } else {
                JSONObject(trimmed).toString()
            }
            validationStatus = "Valid JSON (Minified)"
        } catch (e: Exception) {
            validationStatus = "Invalid JSON: ${e.message}"
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
            value = inputJson,
            onValueChange = {
                inputJson = it
                validationStatus = null
            },
            label = { Text("Input JSON") },
            modifier = Modifier.fillMaxWidth().height(160.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace),
        )

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = indentSpaces == 2,
                onClick = { indentSpaces = 2 },
                label = { Text("2 Spaces") },
            )
            FilterChip(
                selected = indentSpaces == 4,
                onClick = { indentSpaces = 4 },
                label = { Text("4 Spaces") },
            )
            Button(onClick = { format(indentSpaces) }) {
                Text("Format / Beautify")
            }
            OutlinedButton(onClick = { minify() }) {
                Text("Minify")
            }
        }

        if (validationStatus != null) {
            val isValid = validationStatus!!.startsWith("Valid")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isValid) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                ),
            ) {
                Text(
                    text = validationStatus!!,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    color = if (isValid) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }

        if (outputJson.isNotEmpty()) {
            OutlinedTextField(
                value = outputJson,
                onValueChange = { outputJson = it },
                label = { Text("Formatted Output") },
                modifier = Modifier.fillMaxWidth().height(220.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace),
            )

            OutlinedButton(
                onClick = { copyToClipboard(context, "JSON", outputJson) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Copy JSON")
            }
        }
    }
}
