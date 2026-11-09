package com.toolbox.conversion.algebra

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun MatrixCalcScreen() {
    var a by remember { mutableStateOf("4") }
    var b by remember { mutableStateOf("2") }
    var c by remember { mutableStateOf("3") }
    var d by remember { mutableStateOf("1") }

    val m00 = a.toDoubleOrNull() ?: 0.0
    val m01 = b.toDoubleOrNull() ?: 0.0
    val m10 = c.toDoubleOrNull() ?: 0.0
    val m11 = d.toDoubleOrNull() ?: 0.0

    val det = (m00 * m11) - (m01 * m10)
    val trace = m00 + m11

    val inv00 = if (det != 0.0) m11 / det else 0.0
    val inv01 = if (det != 0.0) -m01 / det else 0.0
    val inv10 = if (det != 0.0) -m10 / det else 0.0
    val inv11 = if (det != 0.0) m00 / det else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format(Locale.US, "det(A) = %.2f", det),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = String.format(Locale.US, "Trace: %.2f • %s", trace, if (det == 0.0) "Singular (No Inverse)" else "Invertible"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text("Matrix A (2x2 Elements)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = a,
                onValueChange = { a = it },
                label = { Text("A[0,0]") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = b,
                onValueChange = { b = it },
                label = { Text("A[0,1]") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = c,
                onValueChange = { c = it },
                label = { Text("A[1,0]") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = d,
                onValueChange = { d = it },
                label = { Text("A[1,1]") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Text("Matrix Inverse A⁻¹", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (det == 0.0) {
                    Text("Matrix is singular (determinant = 0), inverse does not exist.", color = MaterialTheme.colorScheme.error)
                } else {
                    Text(
                        String.format(Locale.US, "⌈  %6.2f   %6.2f  ⌉", inv00, inv01),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        String.format(Locale.US, "⌊  %6.2f   %6.2f  ⌋", inv10, inv11),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text("Matrix Transpose Aᵀ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    String.format(Locale.US, "⌈  %6.2f   %6.2f  ⌉", m00, m10),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    String.format(Locale.US, "⌊  %6.2f   %6.2f  ⌋", m01, m11),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
