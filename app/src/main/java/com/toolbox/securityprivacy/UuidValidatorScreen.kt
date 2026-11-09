package com.toolbox.securityprivacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun UuidValidatorScreen() {
    var uuidInput by remember { mutableStateOf("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d") }

    data class Analysis(
        val isValid: Boolean,
        val version: String,
        val variant: String,
        val details: String
    )

    fun analyzeUuid(raw: String): Analysis {
        val clean = raw.trim()
        val regex = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-([0-9a-fA-F])[0-9a-fA-F]{3}-([0-9a-fA-F])[0-9a-fA-F]{3}-[0-9a-fA-F]{12}\$")
        val match = regex.matchEntire(clean)
        if (match == null) {
            return Analysis(false, "Invalid", "N/A", "String is not a valid 36-char hyphenated UUID representation.")
        }
        val verChar = match.groupValues[1]
        val varChar = match.groupValues[2].lowercase()

        val version = when (verChar) {
            "1" -> "Version 1 (Date-time & MAC address)"
            "2" -> "Version 2 (DCE Security)"
            "3" -> "Version 3 (MD5 hash & namespace)"
            "4" -> "Version 4 (Randomly generated)"
            "5" -> "Version 5 (SHA-1 hash & namespace)"
            "7" -> "Version 7 (Unix Epoch time-ordered)"
            else -> "Custom / Unknown (v$verChar)"
        }

        val variant = when (varChar) {
            "8", "9", "a", "b" -> "Leach-Salz / RFC 4122 (Standard)"
            "c", "d" -> "Microsoft Corporation backward compatibility"
            "e", "f" -> "Reserved for future definition"
            else -> "NCS backward compatibility"
        }

        return Analysis(true, version, variant, "Complies with standard UUID hex-and-hyphen layout.")
    }

    val analysis = remember(uuidInput) { analyzeUuid(uuidInput) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (analysis.isValid) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = if (analysis.isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (analysis.isValid) "Valid UUID" else "Invalid UUID",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (analysis.isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = analysis.version,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedTextField(
            value = uuidInput,
            onValueChange = { uuidInput = it },
            label = { Text("UUID String to inspect") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { uuidInput = UUID.randomUUID().toString() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Random v4 UUID Sample")
        }

        Text("Token Specifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Version", fontWeight = FontWeight.Bold)
                    Text(analysis.version)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Variant", fontWeight = FontWeight.Bold)
                    Text(analysis.variant)
                }
                Text("Notes: ${analysis.details}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
