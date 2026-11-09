package com.toolbox.securityprivacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.security.MessageDigest

@Composable
fun ChecksumVerifierScreen() {
    val clipboardManager = LocalClipboardManager.current
    var inputText by remember { mutableStateOf("Quick brown fox jumps over the lazy dog") }
    var expectedHash by remember { mutableStateOf("") }
    var selectedAlgorithm by remember { mutableStateOf("SHA-256") }

    fun computeHash(text: String, algo: String): String {
        return try {
            val digest = MessageDigest.getInstance(algo)
            val bytes = digest.digest(text.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    val computedHash = remember(inputText, selectedAlgorithm) {
        computeHash(inputText, selectedAlgorithm)
    }

    val matches = expectedHash.isNotBlank() && expectedHash.trim().equals(computedHash, ignoreCase = true)
    val mismatches = expectedHash.isNotBlank() && !matches

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("MD5", "SHA-1", "SHA-256", "SHA-512").forEach { algo ->
                FilterChip(
                    selected = selectedAlgorithm == algo,
                    onClick = { selectedAlgorithm = algo },
                    label = { Text(algo, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Input Text / Content") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = expectedHash,
            onValueChange = { expectedHash = it },
            label = { Text("Expected Checksum (Optional for verification)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (matches) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Checksum MATCHES perfectly!", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                }
            }
        } else if (mismatches) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFC62828))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Checksum DOES NOT MATCH", color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Computed $selectedAlgorithm Hash", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                SelectionContainer {
                    Text(
                        text = computedHash,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { clipboardManager.setText(AnnotatedString(computedHash)) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Copy Hash")
                }
            }
        }
    }
}
