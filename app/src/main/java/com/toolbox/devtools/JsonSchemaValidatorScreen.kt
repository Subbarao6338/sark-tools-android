package com.toolbox.devtools

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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Composable
fun JsonSchemaValidatorScreen() {
    var jsonPayload by remember { mutableStateOf("{\n  \"userId\": 101,\n  \"name\": \"Alice\",\n  \"isActive\": true\n}") }
    var requiredKeysText by remember { mutableStateOf("userId, name, isActive") }
    var validationResult by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    fun validate() {
        try {
            val element = Json.parseToJsonElement(jsonPayload.trim())
            if (element !is JsonObject) {
                validationResult = "Validation Failed: Root JSON payload must be an object."
                isSuccess = false
                return
            }

            val required = requiredKeysText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val missingKeys = required.filter { !element.containsKey(it) }

            if (missingKeys.isNotEmpty()) {
                validationResult = "Validation Failed: Missing required properties: ${missingKeys.joinToString(", ")}"
                isSuccess = false
            } else {
                validationResult = "Validation Passed: All ${required.size} required properties exist and JSON syntax is valid."
                isSuccess = true
            }
        } catch (e: Exception) {
            validationResult = "Syntax Error: ${e.message}"
            isSuccess = false
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
                Text("JSON Payload Schema & Property Validator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Verify structure conformance, mandatory schema attributes, and syntactic validity offline.")
            }
        }

        OutlinedTextField(
            value = requiredKeysText,
            onValueChange = { requiredKeysText = it },
            label = { Text("Required Properties (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = jsonPayload,
            onValueChange = { jsonPayload = it },
            label = { Text("JSON Document") },
            modifier = Modifier.fillMaxWidth().height(160.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
        )

        Button(onClick = { validate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Validate Conformance")
        }

        validationResult?.let { msg ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        msg,
                        fontWeight = FontWeight.Bold,
                        color = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
