package com.toolbox.securityprivacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SteganographyScreen() {
    val clipboardManager = LocalClipboardManager.current
    var isEncodeMode by remember { mutableStateOf(true) }

    val ZW_ZERO = '\u200B'
    val ZW_ONE = '\u200C'
    val ZW_SEP = '\u200D'

    var coverText by remember { mutableStateOf("This looks like a completely ordinary paragraph.") }
    var secretText by remember { mutableStateOf("Meet at midnight") }
    var encodedResult by remember { mutableStateOf("") }

    var stegoInput by remember { mutableStateOf("") }
    var revealedSecret by remember { mutableStateOf("") }

    fun encodeSecret(cover: String, secret: String): String {
        if (cover.isEmpty() || secret.isEmpty()) return cover
        val binary = secret.toByteArray(Charsets.UTF_8).joinToString("") { byte ->
            String.format("%8s", Integer.toBinaryString(byte.toInt() and 0xFF)).replace(' ', '0')
        }
        val hiddenChars = binary.map { if (it == '1') ZW_ONE else ZW_ZERO }.joinToString("")
        val spaceIndex = cover.indexOf(' ')
        return if (spaceIndex >= 0) {
            cover.substring(0, spaceIndex + 1) + hiddenChars + ZW_SEP + cover.substring(spaceIndex + 1)
        } else {
            cover + hiddenChars + ZW_SEP
        }
    }

    fun decodeSecret(stego: String): String {
        val hiddenBits = StringBuilder()
        for (ch in stego) {
            if (ch == ZW_ZERO) hiddenBits.append('0')
            else if (ch == ZW_ONE) hiddenBits.append('1')
            else if (ch == ZW_SEP) break
        }
        val bits = hiddenBits.toString()
        if (bits.isEmpty() || bits.length % 8 != 0) return if (bits.isEmpty()) "No hidden message found" else "Malformed hidden payload"
        return try {
            val bytes = ByteArray(bits.length / 8)
            for (i in bytes.indices) {
                val byteStr = bits.substring(i * 8, (i + 1) * 8)
                bytes[i] = byteStr.toInt(2).toByte()
            }
            String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            "Decoding failed: ${e.message}"
        }
    }

    LaunchedEffect(coverText, secretText) {
        encodedResult = encodeSecret(coverText, secretText)
    }

    LaunchedEffect(stegoInput) {
        revealedSecret = decodeSecret(stegoInput)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = isEncodeMode,
                onClick = { isEncodeMode = true },
                label = { Text("Hide Message") },
                leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isEncodeMode,
                onClick = { isEncodeMode = false },
                label = { Text("Extract Hidden") },
                leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
        }

        if (isEncodeMode) {
            OutlinedTextField(
                value = coverText,
                onValueChange = { coverText = it },
                label = { Text("Visible Public Cover Text") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = secretText,
                onValueChange = { secretText = it },
                label = { Text("Secret Payload to Conceal") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Carrier Text (Payload Invisible)", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    SelectionContainer {
                        Text(encodedResult, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { clipboardManager.setText(AnnotatedString(encodedResult)) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Copy Concealed Text")
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = stegoInput,
                onValueChange = { stegoInput = it },
                label = { Text("Paste Text Containing Hidden Payload") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Revealed Hidden Message", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    SelectionContainer {
                        Text(
                            text = revealedSecret,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
