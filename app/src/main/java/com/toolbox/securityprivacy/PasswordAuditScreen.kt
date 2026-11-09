package com.toolbox.securityprivacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.log2

@Composable
fun PasswordAuditScreen() {
    var password by remember { mutableStateOf("") }

    val length = password.length
    val hasLower = password.any { it.isLowerCase() }
    val hasUpper = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }

    val poolSize = (if (hasLower) 26 else 0) +
            (if (hasUpper) 26 else 0) +
            (if (hasDigit) 10 else 0) +
            (if (hasSpecial) 32 else 0)

    val entropyBits = if (poolSize > 0 && length > 0) length * log2(poolSize.toDouble()) else 0.0

    val (rating, color) = when {
        entropyBits >= 80 -> "Very Strong" to Color(0xFF2E7D32)
        entropyBits >= 60 -> "Strong" to Color(0xFF4CAF50)
        entropyBits >= 40 -> "Moderate" to Color(0xFFFFA000)
        entropyBits >= 28 -> "Weak" to Color(0xFFFF5722)
        else -> "Very Weak" to Color(0xFFD32F2F)
    }

    val crackTime = when {
        password.isEmpty() -> "Instant"
        entropyBits < 30 -> "< 1 second"
        entropyBits < 45 -> "A few minutes"
        entropyBits < 55 -> "A few days"
        entropyBits < 65 -> "Several months"
        entropyBits < 80 -> "Centuries"
        else -> "Millions of years"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rating,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = String.format(Locale.US, "%.1f bits of entropy • Crack time: %s", entropyBits, crackTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter password to test") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Security Criteria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CriteriaRow("At least 12 characters", length >= 12)
                CriteriaRow("Contains lowercase letters (a-z)", hasLower)
                CriteriaRow("Contains uppercase letters (A-Z)", hasUpper)
                CriteriaRow("Contains numbers (0-9)", hasDigit)
                CriteriaRow("Contains special symbols (!@#$%^&*)", hasSpecial)
            }
        }
    }
}

@Composable
private fun CriteriaRow(label: String, passed: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Icon(
            imageVector = if (passed) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (passed) Color(0xFF2E7D32) else Color(0xFFD32F2F)
        )
    }
}
