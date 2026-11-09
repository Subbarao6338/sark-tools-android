package com.toolbox.securityprivacy

import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@Composable
fun AesEncryptionScreen() {
    val clipboardManager = LocalClipboardManager.current
    var isEncryptMode by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("Confidential message") }
    var password by remember { mutableStateOf("SecretPassphrase123!") }
    var resultText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun process() {
        errorMessage = null
        if (password.isEmpty()) {
            errorMessage = "Password cannot be empty"
            return
        }
        try {
            if (isEncryptMode) {
                val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
                val iv = ByteArray(12).apply { SecureRandom().nextBytes(this) }
                val spec = PBEKeySpec(password.toCharArray(), salt, 10000, 256)
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val keyBytes = factory.generateSecret(spec).encoded
                val secretKey = SecretKeySpec(keyBytes, "AES")

                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                val gcmSpec = GCMParameterSpec(128, iv)
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
                val cipherBytes = cipher.doFinal(inputText.toByteArray(Charsets.UTF_8))

                val combined = ByteArray(salt.size + iv.size + cipherBytes.size)
                System.arraycopy(salt, 0, combined, 0, salt.size)
                System.arraycopy(iv, 0, combined, salt.size, iv.size)
                System.arraycopy(cipherBytes, 0, combined, salt.size + iv.size, cipherBytes.size)

                resultText = Base64.encodeToString(combined, Base64.NO_WRAP)
            } else {
                val decoded = Base64.decode(inputText.trim(), Base64.NO_WRAP)
                if (decoded.size < 28) {
                    errorMessage = "Invalid encrypted payload format"
                    return
                }
                val salt = ByteArray(16)
                val iv = ByteArray(12)
                val cipherBytes = ByteArray(decoded.size - 28)
                System.arraycopy(decoded, 0, salt, 0, 16)
                System.arraycopy(decoded, 16, iv, 0, 12)
                System.arraycopy(decoded, 28, cipherBytes, 0, cipherBytes.size)

                val spec = PBEKeySpec(password.toCharArray(), salt, 10000, 256)
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val keyBytes = factory.generateSecret(spec).encoded
                val secretKey = SecretKeySpec(keyBytes, "AES")

                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                val gcmSpec = GCMParameterSpec(128, iv)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
                val plainBytes = cipher.doFinal(cipherBytes)
                resultText = String(plainBytes, Charsets.UTF_8)
            }
        } catch (e: Exception) {
            errorMessage = "Operation failed: ${e.message}"
        }
    }

    LaunchedEffect(isEncryptMode, inputText, password) {
        process()
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
                selected = isEncryptMode,
                onClick = { isEncryptMode = true },
                label = { Text("AES-256 Encrypt") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isEncryptMode,
                onClick = { isEncryptMode = false },
                label = { Text("AES-256 Decrypt") },
                leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text(if (isEncryptMode) "Plain Text to Encrypt" else "Base64 Ciphertext to Decrypt") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passphrase / Key") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Text("Output Result", style = MaterialTheme.typography.titleMedium)

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SelectionContainer {
                    Text(
                        text = resultText.ifEmpty { "(None)" },
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { clipboardManager.setText(AnnotatedString(resultText)) },
                    modifier = Modifier.align(Alignment.End),
                    enabled = resultText.isNotEmpty()
                ) {
                    Text("Copy Result")
                }
            }
        }
    }
}
