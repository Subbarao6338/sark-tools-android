package com.toolbox.conversion.numberbase

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun BinaryBitwiseScreen() {
    var valAInput by remember { mutableStateOf("42") }
    var valBInput by remember { mutableStateOf("15") }
    var shiftAmountInput by remember { mutableStateOf("2") }

    val a = valAInput.toLongOrNull() ?: 42L
    val b = valBInput.toLongOrNull() ?: 15L
    val shift = (shiftAmountInput.toIntOrNull() ?: 2).coerceIn(0, 63)

    fun toBin32(n: Long): String {
        val bin = java.lang.Long.toBinaryString(n and 0xFFFFFFFFL)
        return String.format(Locale.US, "%32s", bin).replace(' ', '0')
    }

    val bitwiseAnd = a and b
    val bitwiseOr = a or b
    val bitwiseXor = a xor b
    val bitwiseNotA = (a.inv()) and 0xFFFFFFFFL
    val shlA = (a shl shift) and 0xFFFFFFFFL
    val shrA = a ushr shift

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = valAInput,
                onValueChange = { valAInput = it },
                label = { Text("Operand A (dec)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = valBInput,
                onValueChange = { valBInput = it },
                label = { Text("Operand B (dec)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = shiftAmountInput,
            onValueChange = { shiftAmountInput = it },
            label = { Text("Shift Bits Count") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Bitwise Operations Results (32-bit)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        listOf(
            "A AND B" to bitwiseAnd,
            "A OR B" to bitwiseOr,
            "A XOR B" to bitwiseXor,
            "NOT A (~A)" to bitwiseNotA,
            "A << $shift (Shift Left)" to shlA,
            "A >>> $shift (Logical Shift Right)" to shrA
        ).forEach { (label, res) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, fontWeight = FontWeight.Bold)
                        Text("Dec: $res", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Text("Hex: 0x${java.lang.Long.toHexString(res).uppercase(Locale.US)}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        toBin32(res).chunked(8).joinToString(" "),
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}
