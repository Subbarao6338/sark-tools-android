package com.toolbox.securityprivacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import java.security.MessageDigest

@Composable
fun FileChecksumScreen() {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("Sark Tools Secure Hash") }
    var md5Hash by remember { mutableStateOf("") }
    var sha1Hash by remember { mutableStateOf("") }
    var sha256Hash by remember { mutableStateOf("") }
    var sha512Hash by remember { mutableStateOf("") }

    fun hash(algo: String, bytes: ByteArray): String {
        val digest = MessageDigest.getInstance(algo).digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun computeHashes() {
        val bytes = inputText.toByteArray(Charsets.UTF_8)
        md5Hash = hash("MD5", bytes)
        sha1Hash = hash("SHA-1", bytes)
        sha256Hash = hash("SHA-256", bytes)
        sha512Hash = hash("SHA-512", bytes)
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
                Text("Multi-Algorithm Cryptographic Hasher", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Instant multi-hash verification supporting MD5, SHA-1, SHA-256, and SHA-512 algorithms.")
            }
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Input String / Payload") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { computeHashes() }, modifier = Modifier.fillMaxWidth()) {
            Text("Compute Cryptographic Hashes")
        }

        if (sha256Hash.isNotBlank()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("MD5:", fontWeight = FontWeight.Bold)
                    Text(md5Hash, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)

                    Text("SHA-1:", fontWeight = FontWeight.Bold)
                    Text(sha1Hash, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)

                    Text("SHA-256:", fontWeight = FontWeight.Bold)
                    Text(sha256Hash, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)

                    Text("SHA-512:", fontWeight = FontWeight.Bold)
                    Text(sha512Hash, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                }
            }

            Button(onClick = { copyToClipboard(context, "SHA256", sha256Hash) }, modifier = Modifier.fillMaxWidth()) {
                Text("Copy SHA-256 Hash")
            }
        }
    }
}
